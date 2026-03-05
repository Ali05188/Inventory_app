<?php

namespace App\Services;

use App\Models\TempAsset;
use App\Models\Asset;
use App\Models\AssetType;
use App\Models\Project;
use App\Models\Supplier;
use App\Models\Location;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Str;

class AssetImportValidator
{
    private array $projectsCache = [];
    private array $projectsByCodeCache = [];
    private array $suppliersCache = [];
    private array $suppliersByNameCache = [];
    private array $locationsCache = [];
    private array $locationsByNameCache = [];
    private array $assetTypesCache = [];
    private array $existingSerialNumbers = [];
    private array $batchSerialNumbers = [];
    private int $currentBatchId = 0;

    /**
     * Validate all pending rows in a batch
     */
    public function validateBatch(int $batchId): array
    {
        $this->currentBatchId = $batchId;
        $this->loadCaches();
        $this->loadBatchSerialNumbers($batchId);

        $stats = [
            'total' => 0,
            'valid' => 0,
            'invalid' => 0,
        ];

        TempAsset::where('import_batch_id', $batchId)
            ->where('validation_status', 'pending')
            ->orderBy('id')
            ->chunk(500, function ($rows) use (&$stats) {
                foreach ($rows as $row) {
                    $stats['total']++;
                    $errors = $this->validateRow($row);

                    if (empty($errors)) {
                        $row->update([
                            'validation_status' => 'valid',
                            'validation_errors' => null,
                        ]);
                        $stats['valid']++;

                        // Mark this serial number as used in batch
                        if (!empty($row->serial_number)) {
                            $this->batchSerialNumbers[$row->serial_number] = $row->id;
                        }
                    } else {
                        $row->update([
                            'validation_status' => 'invalid',
                            'validation_errors' => json_encode($errors),
                        ]);
                        $stats['invalid']++;
                    }
                }
            });

        return $stats;
    }

    /**
     * Load reference data caches for performance
     */
    private function loadCaches(): void
    {
        $this->projectsCache = Project::pluck('id', 'code')->toArray();
        $this->projectsByCodeCache = Project::pluck('code', 'id')->toArray();

        $this->suppliersCache = Supplier::where('is_active', true)->pluck('id', 'code')->toArray();
        $this->suppliersByNameCache = Supplier::where('is_active', true)->pluck('id', 'name')->toArray();

        $this->locationsCache = Location::where('is_active', true)->pluck('id', 'code')->toArray();
        $this->locationsByNameCache = Location::where('is_active', true)->pluck('id', 'name')->toArray();

        $this->assetTypesCache = AssetType::pluck('id', 'name')->toArray();

        $this->existingSerialNumbers = Asset::whereNotNull('serial_number')
            ->pluck('serial_number')
            ->flip()
            ->toArray();
    }

    /**
     * Load serial numbers already in this batch (for duplicate detection)
     */
    private function loadBatchSerialNumbers(int $batchId): void
    {
        $this->batchSerialNumbers = TempAsset::where('import_batch_id', $batchId)
            ->whereNotNull('serial_number')
            ->where('validation_status', 'valid')
            ->pluck('id', 'serial_number')
            ->toArray();
    }

    /**
     * Validate a single row
     */
    private function validateRow(TempAsset $row): array
    {
        $errors = [];

        // Required field validations
        $errors = array_merge($errors, $this->validateRequiredFields($row));

        // Business rule validations
        $errors = array_merge($errors, $this->validateBusinessRules($row));

        // Duplicate validations (in DB and in batch)
        $errors = array_merge($errors, $this->validateDuplicates($row));

        return $errors;
    }

    /**
     * Validate required fields
     */
    private function validateRequiredFields(TempAsset $row): array
    {
        $errors = [];

        if (empty($row->cab_number)) {
            $errors[] = [
                'field' => 'cab_number',
                'message' => 'CAB number is required',
                'code' => 'REQUIRED_FIELD',
            ];
        }

        if (empty($row->project_code)) {
            $errors[] = [
                'field' => 'project_code',
                'message' => 'Project code is required',
                'code' => 'REQUIRED_FIELD',
            ];
        }

        if (empty($row->asset_number)) {
            $errors[] = [
                'field' => 'asset_number',
                'message' => 'Asset number is required',
                'code' => 'REQUIRED_FIELD',
            ];
        }

        if (empty($row->designation)) {
            $errors[] = [
                'field' => 'designation',
                'message' => 'Designation is required',
                'code' => 'REQUIRED_FIELD',
            ];
        }

        return $errors;
    }

    /**
     * Validate business rules
     */
    private function validateBusinessRules(TempAsset $row): array
    {
        $errors = [];

        if (!is_null($row->quantity) && $row->quantity <= 0) {
            $errors[] = [
                'field' => 'quantity',
                'message' => 'Quantity must be a positive number',
                'code' => 'INVALID_VALUE',
            ];
        }

        if (!is_null($row->unit_price) && $row->unit_price < 0) {
            $errors[] = [
                'field' => 'unit_price',
                'message' => 'Unit price cannot be negative',
                'code' => 'INVALID_VALUE',
            ];
        }

        return $errors;
    }

    /**
     * Validate duplicates (in existing assets AND in current batch)
     */
    private function validateDuplicates(TempAsset $row): array
    {
        $errors = [];

        if (!empty($row->serial_number)) {
            // Check in existing assets
            if (isset($this->existingSerialNumbers[$row->serial_number])) {
                $errors[] = [
                    'field' => 'serial_number',
                    'message' => "Serial number '{$row->serial_number}' already exists in the system",
                    'code' => 'DUPLICATE_ENTRY',
                ];
            }
            // Check in current batch (already validated rows)
            elseif (isset($this->batchSerialNumbers[$row->serial_number]) &&
                    $this->batchSerialNumbers[$row->serial_number] !== $row->id) {
                $errors[] = [
                    'field' => 'serial_number',
                    'message' => "Serial number '{$row->serial_number}' is duplicated in this import batch",
                    'code' => 'DUPLICATE_IN_BATCH',
                ];
            }
        }

        return $errors;
    }

    /**
     * Get or create supplier by name
     */
    private function getOrCreateSupplier(?string $supplierName): ?int
    {
        if (empty($supplierName)) {
            return null;
        }

        // Check cache first
        if (isset($this->suppliersByNameCache[$supplierName])) {
            return $this->suppliersByNameCache[$supplierName];
        }

        // Create new supplier
        $code = 'SUP-' . Str::upper(Str::slug(Str::limit($supplierName, 20), '-'));
        $code = $this->makeUniqueCode($code, 'suppliers', 'code');

        $supplier = Supplier::create([
            'code' => $code,
            'name' => $supplierName,
            'is_active' => true,
        ]);

        // Update cache
        $this->suppliersByNameCache[$supplierName] = $supplier->id;
        $this->suppliersCache[$code] = $supplier->id;

        Log::info("Auto-created supplier: {$supplierName} (ID: {$supplier->id})");

        return $supplier->id;
    }

    /**
     * Get or create project by code
     */
    private function getOrCreateProject(?string $projectCode, ?string $projectDescription = null): ?int
    {
        if (empty($projectCode)) {
            return null;
        }

        // Check cache first
        if (isset($this->projectsCache[$projectCode])) {
            return $this->projectsCache[$projectCode];
        }

        // Create new project
        $project = Project::create([
            'code' => $projectCode,
            'name' => $projectDescription ?? $projectCode,
            'status' => 'active',
        ]);

        // Update cache
        $this->projectsCache[$projectCode] = $project->id;

        Log::info("Auto-created project: {$projectCode} (ID: {$project->id})");

        return $project->id;
    }

    /**
     * Get or create location by name
     */
    private function getOrCreateLocation(?string $locationName): ?int
    {
        if (empty($locationName)) {
            return null;
        }

        // Check cache first
        if (isset($this->locationsByNameCache[$locationName])) {
            return $this->locationsByNameCache[$locationName];
        }

        // Create new location
        $code = 'LOC-' . Str::upper(Str::slug(Str::limit($locationName, 20), '-'));
        $code = $this->makeUniqueCode($code, 'locations', 'code');

        $location = Location::create([
            'code' => $code,
            'name' => $locationName,
            'is_active' => true,
        ]);

        // Update cache
        $this->locationsByNameCache[$locationName] = $location->id;
        $this->locationsCache[$code] = $location->id;

        Log::info("Auto-created location: {$locationName} (ID: {$location->id})");

        return $location->id;
    }

    /**
     * Get or create asset type by name
     */
    private function getOrCreateAssetType(?string $typeName): ?int
    {
        if (empty($typeName)) {
            return null;
        }

        // Check cache first
        if (isset($this->assetTypesCache[$typeName])) {
            return $this->assetTypesCache[$typeName];
        }

        // Create new asset type
        $assetType = AssetType::create([
            'name' => $typeName,
        ]);

        // Update cache
        $this->assetTypesCache[$typeName] = $assetType->id;

        Log::info("Auto-created asset type: {$typeName} (ID: {$assetType->id})");

        return $assetType->id;
    }

    /**
     * Make a unique code by appending numbers if needed
     */
    private function makeUniqueCode(string $baseCode, string $table, string $column): string
    {
        $code = $baseCode;
        $counter = 1;

        while (DB::table($table)->where($column, $code)->exists()) {
            $code = $baseCode . '-' . $counter;
            $counter++;
        }

        return $code;
    }

    /**
     * Move valid rows to assets table with foreign key linking
     * PART 3: Wrapped in DB transactions for data integrity
     */
    public function moveValidRowsToAssets(int $batchId): int
    {
        $this->loadCaches(); // Refresh caches
        $movedCount = 0;

        TempAsset::where('import_batch_id', $batchId)
            ->where('validation_status', 'valid')
            ->chunk(500, function ($rows) use (&$movedCount) {
                foreach ($rows as $tempAsset) {
                    try {
                        // PART 3: Wrap each asset creation in a transaction
                        DB::transaction(function () use ($tempAsset, &$movedCount) {
                            // Get or create related entities (inside transaction)
                            $supplierId = $this->getOrCreateSupplier($tempAsset->supplier_name);
                            $projectId = $this->getOrCreateProject($tempAsset->project_code, $tempAsset->project_description);
                            $locationId = $this->getOrCreateLocation($tempAsset->localisation);
                            $assetTypeId = $this->getOrCreateAssetType($tempAsset->asset_type_name);

                            Asset::create([
                                'cab_number' => $tempAsset->cab_number,
                                'project_code' => $tempAsset->project_code,
                                'asset_number' => $tempAsset->asset_number,
                                'designation' => $tempAsset->designation,
                                'serial_number' => $tempAsset->serial_number,
                                'quantity' => $tempAsset->quantity,
                                'unit_price' => $tempAsset->unit_price,
                                'delivery_date' => $tempAsset->delivery_date,
                                'supplier_id' => $supplierId,
                                'project_id' => $projectId,
                                'location_id' => $locationId,
                                'asset_type_id' => $assetTypeId,
                            ]);

                            $movedCount++;
                        });
                    } catch (\Exception $e) {
                        Log::error("Failed to move temp asset {$tempAsset->id}: " . $e->getMessage());
                    }
                }
            });

        return $movedCount;
    }

    /**
     * Get validation summary for a batch
     */
    public function getBatchValidationSummary(int $batchId): array
    {
        return [
            'total' => TempAsset::where('import_batch_id', $batchId)->count(),
            'pending' => TempAsset::where('import_batch_id', $batchId)->where('validation_status', 'pending')->count(),
            'valid' => TempAsset::where('import_batch_id', $batchId)->where('validation_status', 'valid')->count(),
            'invalid' => TempAsset::where('import_batch_id', $batchId)->where('validation_status', 'invalid')->count(),
        ];
    }

    /**
     * Get invalid rows with errors for a batch
     */
    public function getInvalidRows(int $batchId): \Illuminate\Database\Eloquent\Collection
    {
        return TempAsset::where('import_batch_id', $batchId)
            ->where('validation_status', 'invalid')
            ->get(['id', 'asset_number', 'cab_number', 'serial_number', 'validation_errors']);
    }
}

