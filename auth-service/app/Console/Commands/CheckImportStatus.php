<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use App\Models\AssetImportBatch;
use App\Models\TempAsset;

class CheckImportStatus extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'assets:status {batchId?}';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Check the status of an import batch';

    /**
     * Execute the console command.
     */
    public function handle(): int
    {
        $batchId = $this->argument('batchId');

        if ($batchId) {
            $batch = AssetImportBatch::find($batchId);
            if (!$batch) {
                $this->error("Batch not found: {$batchId}");
                return 1;
            }
            $this->displayBatchDetails($batch);
        } else {
            $this->displayAllBatches();
        }

        return 0;
    }

    private function displayBatchDetails(AssetImportBatch $batch): void
    {
        $this->info("=== BATCH #{$batch->id} ===");
        $this->newLine();

        $this->table(
            ['Field', 'Value'],
            [
                ['ID', $batch->id],
                ['File', $batch->file_name],
                ['Status', $this->formatStatus($batch->status)],
                ['Total Rows', $batch->total_rows],
                ['Processed', $batch->processed_rows ?? 'N/A'],
                ['Success', $batch->success_rows],
                ['Failed', $batch->failed_rows],
                ['Execution Time', ($batch->execution_time_seconds ?? 0) . 's'],
                ['Started At', $batch->started_at ?? 'N/A'],
                ['Completed At', $batch->completed_at ?? 'N/A'],
                ['Created By', $batch->created_by],
            ]
        );

        if ($batch->error_summary) {
            $this->newLine();
            $this->error("Error Summary:");
            $errors = json_decode($batch->error_summary, true);
            $this->line("  " . ($errors['message'] ?? 'Unknown error'));
        }

        // Show validation stats from temp_assets
        $this->newLine();
        $this->info("Validation Breakdown:");
        $stats = TempAsset::where('import_batch_id', $batch->id)
            ->selectRaw('validation_status, COUNT(*) as count')
            ->groupBy('validation_status')
            ->pluck('count', 'validation_status')
            ->toArray();

        $this->table(
            ['Status', 'Count'],
            collect($stats)->map(fn($count, $status) => [$status, $count])->toArray()
        );
    }

    private function displayAllBatches(): void
    {
        $batches = AssetImportBatch::orderBy('created_at', 'desc')->take(10)->get();

        if ($batches->isEmpty()) {
            $this->info("No import batches found.");
            return;
        }

        $this->info("=== RECENT IMPORT BATCHES ===");
        $this->newLine();

        $rows = $batches->map(fn($b) => [
            $b->id,
            $b->file_name,
            $this->formatStatus($b->status),
            $b->total_rows,
            $b->success_rows,
            $b->failed_rows,
            ($b->execution_time_seconds ?? 0) . 's',
            $b->created_at->format('Y-m-d H:i'),
        ])->toArray();

        $this->table(
            ['ID', 'File', 'Status', 'Total', 'Success', 'Failed', 'Time', 'Created'],
            $rows
        );
    }

    private function formatStatus(string $status): string
    {
        return match($status) {
            'pending' => '⏳ Pending',
            'processing' => '🔄 Processing',
            'completed' => '✅ Completed',
            'failed' => '❌ Failed',
            default => $status,
        };
    }
}
