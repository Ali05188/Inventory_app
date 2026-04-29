<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();
try {
    $batch = App\Models\AssetImportBatch::latest()->first();
    $data = App\Models\TempAsset::where('import_batch_id', $batch->id)->where('validation_status', 'invalid')->take(3)->get()->toArray();
    echo "COUNT: " . count($data) . "\n";
    print_r($data);
} catch (\Exception $e) {
    echo "ERROR: " . $e->getMessage() . "\n";
}

