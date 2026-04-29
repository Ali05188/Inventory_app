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
$t1 = array_map(function($t){return array_values((array)$t)[0];}, DB::connection('custom')->select('SHOW TABLES'));
$out = "auth_db: " . implode(',', $t1) . "\n";

Config::set('database.connections.custom2', [
    'driver' => 'mysql',
    'host' => '127.0.0.1',
    'port' => '3306',
    'database' => 'inventory_assets',
    'username' => 'root',
    'password' => '',
]);
$t2 = array_map(function($t){return array_values((array)$t)[0];}, DB::connection('custom2')->select('SHOW TABLES'));
$out .= "inventory_assets: " . implode(',', $t2) . "\n";

file_put_contents('final_dbs.txt', $out);

