<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use Illuminate\Support\Facades\Config;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

// Clean up auth_db
Config::set('database.connections.custom', [
    'driver' => 'mysql',
    'host' => '127.0.0.1',
    'port' => '3306',
    'database' => 'auth_db',
    'username' => 'root', // assuming root
    'password' => '',
]);

try {
    DB::connection('custom')->statement('SET FOREIGN_KEY_CHECKS=0;');

    $unneededInAuth = [
        'assets',
        'asset_import_batches',
        'asset_status_histories',
        'asset_types',
        'locations',
        'projects',
        'suppliers',
        'temp_assets'
    ];

    foreach ($unneededInAuth as $table) {
        Schema::connection('custom')->dropIfExists($table);
        echo "Dropped $table from auth_db\n";
    }

    DB::connection('custom')->statement('SET FOREIGN_KEY_CHECKS=1;');

} catch (\Exception $e) {
    echo "Error on auth_db: " . $e->getMessage() . "\n";
}

// Clean up inventory_assets
Config::set('database.connections.custom2', [
    'driver' => 'mysql',
    'host' => '127.0.0.1',
    'port' => '3306',
    'database' => 'inventory_assets',
    'username' => 'root',
    'password' => '',
]);

try {
    DB::connection('custom2')->statement('SET FOREIGN_KEY_CHECKS=0;');

    $unneededInInventory = [
        'users',
        'cache',
        'cache_locks',
        'failed_jobs',
        'jobs',
        'job_batches',
        'migrations',
        'model_has_permissions',
        'model_has_roles',
        'password_reset_tokens',
        'permissions',
        'roles',
        'role_has_permissions',
        'sessions'
    ];

    foreach ($unneededInInventory as $table) {
        Schema::connection('custom2')->dropIfExists($table);
        echo "Dropped $table from inventory_assets\n";
    }

    DB::connection('custom2')->statement('SET FOREIGN_KEY_CHECKS=1;');
} catch (\Exception $e) {
    echo "Error on inventory_assets: " . $e->getMessage() . "\n";
}

