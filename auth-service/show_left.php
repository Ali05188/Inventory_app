<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use Illuminate\Support\Facades\Config;
use Illuminate\Support\Facades\DB;

Config::set('database.connections.custom', [
    'driver' => 'mysql',
    'host' => '127.0.0.1',
    'port' => '3306',
    'database' => 'auth_db',
    'username' => 'root',
    'password' => '',
]);

try {
    $tables = DB::connection('custom')->select('SHOW TABLES');
    echo "auth_db tables:\n";
    foreach ($tables as $t) {
        $arr = array_values((array)$t);
        echo "- " . $arr[0] . "\n";
    }
} catch (\Exception $e) {}

Config::set('database.connections.custom2', [
    'driver' => 'mysql',
    'host' => '127.0.0.1',
    'port' => '3306',
    'database' => 'inventory_assets',
    'username' => 'root',
    'password' => '',
]);

try {
    $tables = DB::connection('custom2')->select('SHOW TABLES');
    echo "\ninventory_assets tables:\n";
    foreach ($tables as $t) {
        $arr = array_values((array)$t);
        echo "- " . $arr[0] . "\n";
    }
} catch (\Exception $e) {}

