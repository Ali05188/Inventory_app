<?php

namespace App\Jobs;

use App\Models\AssetImportBatch;
use App\Models\TempAsset;
use App\Services\AssetImportValidator;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Carbon\Carbon;

class ProcessAssetImport implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    /**
     * Number of times the job may be attempted.
     */
    public int $tries = 3;

    /**
     * Number of seconds the job can run before timing out.
     */
    public int $timeout = 3600; // 1 hour for large imports

    /**
     * Batch ID to process
     */
    private int $batchId;

    /**
     * Path to CSV file
     */
    private string $filePath;

    /**
     * CSV separator
     */
    private string $separator;

    /**
     * CSV Column Index Mapping
     */
    private const COLUMN_MAP = [
        'cab_number' => 0,
        'project_code' => 1,
        'asset_number' => 2,
        'asset_type_name' => 3,
        'supplier_name' => 4,
        'article_code' => 5,
        'designation' => 6,
        'description' => 7,
        'serial_number' => 8,
        'order_number' => 9,
        'section_analytique' => 10,
        'zone' => 11,
        'localisation' => 12,
        'quantity' => 13,
        'unit_price' => 14,
        'delivery_date' => 17,
        'service_start_date' => 18,
        'exit_date' => 19,
        'status' => 20,
        'project_description' => 24,
    ];

    /**
     * Create a new job instance.
     */
    public function __construct(int $batchId, string $filePath, string $separator = ';')
    {
        $this->batchId = $batchId;
        $this->filePath = $filePath;
        $this->separator = $separator;
    }

    /**
     * Execute the job.
     */
    public function handle(): void
    {
        $startTime = microtime(true);
        $batch = AssetImportBatch::findOrFail($this->batchId);

        Log::info("Starting import job for batch {$this->batchId}", [
            'file' => $this->filePath,
            'batch_id' => $this->batchId,
        ]);

        try {
            // Update batch status to processing
            $batch->update([
                'status' => 'processing',
                'started_at' => Carbon::now(),
            ]);

            // PART 2: Chunk CSV Processing (Memory Safety)
            $stats = $this->processCSVInChunks();

            // Run validation
            Log::info("Starting validation for batch {$this->batchId}");
            $validator = new AssetImportValidator();
            $validationStats = $validator->validateBatch($this->batchId);

            // Move valid rows to assets (with transactions - PART 3)
            $movedCount = $validator->moveValidRowsToAssets($this->batchId);

            // Calculate execution time
            $executionTime = round(microtime(true) - $startTime, 2);

            // Update batch with final stats
            $batch->update([
                'status' => 'completed',
                'total_rows' => $stats['total'],
                'processed_rows' => $stats['processed'],
                'success_rows' => $validationStats['valid'],
                'failed_rows' => $validationStats['invalid'],
                'execution_time_seconds' => $executionTime,
                'completed_at' => Carbon::now(),
            ]);

            Log::info("Import completed for batch {$this->batchId}", [
                'total_rows' => $stats['total'],
                'valid' => $validationStats['valid'],
                'invalid' => $validationStats['invalid'],
                'moved_to_assets' => $movedCount,
                'execution_time' => $executionTime,
            ]);

        } catch (\Exception $e) {
            $this->handleFailure($batch, $e, $startTime);
            throw $e; // Re-throw for queue retry mechanism
        }
    }

    /**
     * Process CSV file in chunks (memory-safe)
     */
    private function processCSVInChunks(): array
    {
        $stats = [
            'total' => 0,
            'processed' => 0,
            'skipped' => 0,
        ];

        if (!file_exists($this->filePath)) {
            throw new \Exception("File not found: {$this->filePath}");
        }

        $file = fopen($this->filePath, 'r');
        if ($file === false) {
            throw new \Exception("Cannot open file: {$this->filePath}");
        }

        // Skip preamble (first 10 rows), header is on the 11th row
        for ($i = 0; $i < 10; $i++) {
            fgetcsv($file, 0, $this->separator);
        }
        $header = fgetcsv($file, 0, $this->separator);
        Log::info("CSV header columns: " . (is_array($header) ? count($header) : 0));

        $batchInserts = [];
        $batchSize = 100; // Insert in batches of 100 for performance

        while (($row = fgetcsv($file, 0, $this->separator)) !== false) {
            $stats['total']++;

            // Include ALL rows (even empty ones)

            // Map row to temp_asset data
            $batchInserts[] = [
                'import_batch_id' => $this->batchId,
                'cab_number' => $this->getValue($row, 'cab_number'),
                'project_code' => $this->getValue($row, 'project_code'),
                'project_description' => $this->getValue($row, 'project_description'),
                'asset_number' => $this->getValue($row, 'asset_number'),
                'asset_type_name' => $this->getValue($row, 'asset_type_name'),
                'supplier_name' => $this->getValue($row, 'supplier_name'),
                'article_code' => $this->getValue($row, 'article_code'),
                'designation' => $this->getValue($row, 'designation'),
                'description' => $this->getValue($row, 'description'),
                'serial_number' => $this->getValue($row, 'serial_number'),
                'order_number' => $this->getValue($row, 'order_number'),
                'section_analytique' => $this->getValue($row, 'section_analytique'),
                'zone' => $this->getValue($row, 'zone'),
                'localisation' => $this->getValue($row, 'localisation'),
                'quantity' => $this->getIntValue($row, 'quantity'),
                'unit_price' => $this->getDecimalValue($row, 'unit_price'),
                'delivery_date' => $this->getDateValue($row, 'delivery_date'),
                'service_start_date' => $this->getDateValue($row, 'service_start_date'),
                'exit_date' => $this->getDateValue($row, 'exit_date'),
                'status' => $this->getValue($row, 'status'),
                'raw_data' => json_encode($row),
                'validation_status' => 'pending',
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ];

            // Insert batch when size reached
            if (count($batchInserts) >= $batchSize) {
                TempAsset::insert($batchInserts);
                $stats['processed'] += count($batchInserts);
                $batchInserts = [];

                // Log progress every 1000 rows
                if ($stats['processed'] % 1000 === 0) {
                    Log::info("Processed {$stats['processed']} rows for batch {$this->batchId}");
                }
            }
        }

        // Insert remaining rows
        if (!empty($batchInserts)) {
            TempAsset::insert($batchInserts);
            $stats['processed'] += count($batchInserts);
        }

        fclose($file);

        Log::info("CSV processing completed for batch {$this->batchId}", $stats);

        return $stats;
    }

    /**
     * Handle job failure
     */
    private function handleFailure(AssetImportBatch $batch, \Exception $e, float $startTime): void
    {
        $executionTime = round(microtime(true) - $startTime, 2);

        $errorSummary = [
            'message' => $e->getMessage(),
            'file' => $e->getFile(),
            'line' => $e->getLine(),
            'trace' => array_slice($e->getTrace(), 0, 5),
        ];

        $batch->update([
            'status' => 'failed',
            'execution_time_seconds' => $executionTime,
            'error_summary' => json_encode($errorSummary),
            'completed_at' => Carbon::now(),
        ]);

        Log::error("Import failed for batch {$this->batchId}", [
            'error' => $e->getMessage(),
            'execution_time' => $executionTime,
        ]);
    }

    /**
     * Handle a job failure after all retries.
     */
    public function failed(\Throwable $exception): void
    {
        $batch = AssetImportBatch::find($this->batchId);
        if ($batch) {
            $batch->update([
                'status' => 'failed',
                'error_summary' => json_encode([
                    'message' => $exception->getMessage(),
                    'final_failure' => true,
                ]),
                'completed_at' => Carbon::now(),
            ]);
        }

        Log::critical("Import job permanently failed for batch {$this->batchId}", [
            'error' => $exception->getMessage(),
        ]);
    }

    /**
     * Get string value from row by column name
     */
    private function getValue(array $row, string $column): ?string
    {
        $index = self::COLUMN_MAP[$column] ?? null;
        if ($index === null || !isset($row[$index])) {
            return null;
        }
        $value = trim($row[$index]);
        return $value !== '' ? $value : null;
    }

    /**
     * Get integer value from row
     */
    private function getIntValue(array $row, string $column): ?int
    {
        $value = $this->getValue($row, $column);
        if ($value === null) {
            return null;
        }
        $value = str_replace([',', ' '], ['.', ''], $value);
        return is_numeric($value) ? (int) $value : null;
    }

    /**
     * Get decimal value from row
     */
    private function getDecimalValue(array $row, string $column): ?float
    {
        $value = $this->getValue($row, $column);
        if ($value === null) {
            return null;
        }
        $value = str_replace([',', ' '], ['.', ''], $value);
        return is_numeric($value) ? (float) $value : null;
    }

    /**
     * Get date value from row
     */
    private function getDateValue(array $row, string $column): ?string
    {
        $value = $this->getValue($row, $column);
        if ($value === null) {
            return null;
        }

        $formats = ['Y-m-d', 'd/m/Y', 'd-m-Y', 'Y/m/d'];
        foreach ($formats as $format) {
            $date = \DateTime::createFromFormat($format, $value);
            if ($date !== false) {
                return $date->format('Y-m-d');
            }
        }

        return null;
    }
}
