<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();
echo "Temp Assets: " . App\Models\TempAsset::count() . "\n";
echo "Temp Assets valid: " . App\Models\TempAsset::where('validation_status', 'valid')->count() . "\n";
echo "Temp Assets skipped: " . App\Models\TempAsset::where('import_batch_id', 1)->whereNull('cab_number')->whereNull('asset_number')->count() . "\n";
echo "Assets (prod): " . App\Models\Asset::count() . "\n";
echo "Batches: " . App\Models\AssetImportBatch::count() . "\n";
