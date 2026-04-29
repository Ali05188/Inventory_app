<?php
ini_set('display_errors', 1); error_reporting(E_ALL);
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

try {
    $c = \App\Models\TempAsset::where('validation_status', 'pending')->count();
    file_put_contents(__DIR__.'/pending_c.txt', "Count is $c");
} catch (\Exception $e) {
    file_put_contents(__DIR__.'/pending_c.txt', "Error: " . $e->getMessage());
}

