<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\AssetController;
use App\Http\Controllers\Admin\UserController;
use App\Http\Controllers\Auth\LoginController;

Route::get('/', function () {
    return redirect('/login');
});

// Auth Routes (Guest only)
Route::middleware('guest')->group(function () {
    Route::get('/login', [LoginController::class, 'showLoginForm'])->name('login');
    Route::post('/login', [LoginController::class, 'login']);
});

// Logout Route
Route::post('/logout', [LoginController::class, 'logout'])->name('logout')->middleware('auth');

// Admin Routes (Protected by auth and role middleware)
Route::prefix('admin')
    ->middleware(['auth', 'role:Super Admin|Asset Manager|Auditor|Finance|Viewer'])
    ->name('admin.')
    ->group(function () {
        Route::get('/dashboard', [DashboardController::class, 'index'])
            ->middleware('permission:view dashboard')
            ->name('dashboard');

        Route::resource('/assets', AssetController::class);

        Route::post('/assets/{asset}/change-status', [AssetController::class, 'changeStatus'])
            ->middleware('permission:change asset status')
            ->name('assets.change-status');

        // User Management Routes
        Route::resource('/users', UserController::class);
    });

