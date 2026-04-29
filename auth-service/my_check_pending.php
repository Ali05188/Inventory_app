<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

$output = "Total temp rows: " . \App\Models\TempAsset::count() . "\n";
$output .= "Pending temp rows: " . \App\Models\TempAsset::where('validation_status', 'pending')->count() . "\n";
$output .= "Valid temp rows: " . \App\Models\TempAsset::where('validation_status', 'valid')->count() . "\n";
$output .= "Invalid temp rows: " . \App\Models\TempAsset::where('validation_status', 'invalid')->count() . "\n";
$output .= "Total assets: " . \App\Models\Asset::count() . "\n";

if (\App\Models\TempAsset::where('validation_status', 'pending')->count() > 0) {
    $output .= "First Pending:\n";
    $output .= print_r(\App\Models\TempAsset::where('validation_status', 'pending')->first()->toArray(), true);
}

file_put_contents('pending_result.txt', $output);

