<?php
require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();
$errors = App\Models\TempAsset::where("validation_status", "invalid")->take(5)->pluck("validation_errors");
foreach($errors as $e) echo $e . "\n";

