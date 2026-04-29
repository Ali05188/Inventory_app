<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

file_put_contents('test_out.txt', "Total Temp: " . \App\Models\TempAsset::count() . "\n" .
                                   "Valid Temp: " . \App\Models\TempAsset::where('validation_status', 'valid')->count() . "\n" .
                                   "Invalid Temp: " . \App\Models\TempAsset::where('validation_status', 'invalid')->count() . "\n" .
                                   "Assets: " . \App\Models\Asset::count() . "\n");

