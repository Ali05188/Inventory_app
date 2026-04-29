<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

echo 'Total Temp: ' . \App\Models\TempAsset::count() . "\n";
echo 'Valid Temp: ' . \App\Models\TempAsset::where('validation_status', 'valid')->count() . "\n";
echo 'Invalid Temp: ' . \App\Models\TempAsset::where('validation_status', 'invalid')->count() . "\n";
echo 'Pending Temp: ' . \App\Models\TempAsset::where('validation_status', 'pending')->count() . "\n";
echo 'Total Assets: ' . \App\Models\Asset::count() . "\n";

echo "Errors sample:\n";
$errors = \App\Models\TempAsset::where('validation_status', 'invalid')->take(5)->pluck('validation_errors')->toArray();
print_r($errors);

