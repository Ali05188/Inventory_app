<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use App\Models\AssetImportBatch;
use App\Jobs\ProcessAssetImport;
use Illuminate\Support\Facades\Auth;

class ImportAssetsFromCsv extends Command
{
    protected $signature = 'assets:import {filename}
                            {--separator=; : CSV separator (default: ;)}
                            {--sync : Run synchronously instead of queued}';

    protected $description = 'Import assets from a CSV file (queued by default for large files)';

    public function handle(): int
    {
        $filename = $this->argument('filename');
        $separator = $this->option('separator');
        $sync = $this->option('sync');
        $path = storage_path("app/imports/{$filename}");

        // Validate file exists
        if (!file_exists($path)) {
            $this->error("File not found: {$filename}");
            return 1;
        }

        // Get file size for info
        $fileSizeKB = round(filesize($path) / 1024, 2);
        $this->info("File: {$filename} ({$fileSizeKB} KB)");
        $this->info("Separator: '{$separator}'");

        // Count rows for estimation
        $lineCount = $this->countLines($path);
        $this->info("Estimated rows: {$lineCount}");

        // Create import batch record
        $batch = AssetImportBatch::create([
            'file_name' => $filename,
            'status' => 'pending',
            'total_rows' => $lineCount - 1, // Exclude header
            'created_by' => Auth::id() ?? 1,
        ]);

        $this->info("Batch created with ID: {$batch->id}");

        if ($sync) {
            // Run synchronously (for testing or small files)
            $this->info("Running import synchronously...");
            $this->newLine();

            try {
                $job = new ProcessAssetImport($batch->id, $path, $separator);
                $job->handle();

                // Refresh batch to get updated stats
                $batch->refresh();

                $this->displayResults($batch);
            } catch (\Exception $e) {
                $this->error("Import failed: " . $e->getMessage());
                return 1;
            }
        } else {
            // Dispatch to queue (default - non-blocking)
            ProcessAssetImport::dispatch($batch->id, $path, $separator);

            $this->info("Import job dispatched to queue.");
            $this->warn("Run 'php artisan queue:work' to process the job.");
            $this->newLine();
            $this->info("Monitor progress with:");
            $this->line("  php artisan assets:status {$batch->id}");
        }

        return 0;
    }

    /**
     * Count lines in file efficiently
     */
    private function countLines(string $path): int
    {
        $lineCount = 0;
        $file = fopen($path, 'r');
        while (!feof($file)) {
            $line = fgets($file);
            if ($line !== false && trim($line) !== '') {
                $lineCount++;
            }
        }
        fclose($file);
        return $lineCount;
    }

    /**
     * Display import results
     */
    private function displayResults(AssetImportBatch $batch): void
    {
        $this->newLine();
        $this->info("=== IMPORT RESULTS ===");
        $this->table(
            ['Metric', 'Value'],
            [
                ['Status', $batch->status],
                ['Total Rows', $batch->total_rows],
                ['Processed', $batch->processed_rows ?? 'N/A'],
                ['Success', $batch->success_rows],
                ['Failed', $batch->failed_rows],
                ['Execution Time', ($batch->execution_time_seconds ?? 0) . ' seconds'],
            ]
        );

        if ($batch->failed_rows > 0) {
            $this->newLine();
            $this->warn("Some rows failed validation. Check temp_assets table for details.");
        }
    }
}

