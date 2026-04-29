<?php
ini_set('display_errors', 1); error_reporting(E_ALL);
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();
use App\Models\Asset;
use App\Models\TempAsset;

$temp = TempAsset::count();
$valid = TempAsset::where('validation_status', 'valid')->count();
$assets = Asset::count();
echo "Temp: $temp, Valid: $valid, Assets: $assets\n";

