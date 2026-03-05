<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

// Check if admin exists
$admin = User::where('email', 'admin@email.com')->first();

if ($admin) {
    echo "Admin already exists!" . PHP_EOL;
    echo "Email: admin@email.com" . PHP_EOL;
    echo "Password: password123" . PHP_EOL;
} else {
    // Create admin user
    $admin = User::create([
        'name' => 'Admin',
        'email' => 'admin@email.com',
        'password' => Hash::make('password123'),
    ]);

    echo "Admin user created!" . PHP_EOL;
    echo "Email: admin@email.com" . PHP_EOL;
    echo "Password: password123" . PHP_EOL;
}

echo PHP_EOL . "Total users: " . User::count() . PHP_EOL;

