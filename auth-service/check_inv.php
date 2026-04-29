<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();
use Illuminate\Support\Facades\DB;

try {
    echo "assets in auth_db: " . json_encode(DB::select("SELECT COUNT(*) as c FROM auth_db.assets")) . "\n";
} catch (\Exception $e) { echo $e->getMessage() . "\n"; }

try {
    echo "temp_assets in auth_db: " . json_encode(DB::select("SELECT COUNT(*) as c FROM auth_db.temp_assets")) . "\n";
} catch (\Exception $e) { echo $e->getMessage() . "\n"; }

try {
    echo "assets in inventory: " . json_encode(DB::select("SELECT COUNT(*) as c FROM inventory.assets")) . "\n";
} catch (\Exception $e) { echo $e->getMessage() . "\n"; }

try {
    echo "temp_assets in inventory: " . json_encode(DB::select("SELECT COUNT(*) as c FROM inventory.temp_assets")) . "\n";
} catch (\Exception $e) { echo $e->getMessage() . "\n"; }

