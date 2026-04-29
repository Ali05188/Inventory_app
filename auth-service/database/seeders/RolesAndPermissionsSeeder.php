<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Spatie\Permission\Models\Role;
use Spatie\Permission\Models\Permission;
use App\Models\User;

class RolesAndPermissionsSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Reset cached roles and permissions
        app()[\Spatie\Permission\PermissionRegistrar::class]->forgetCachedPermissions();

        // ========================================
        // CREATE PERMISSIONS
        // ========================================

        // Asset Permissions
        Permission::firstOrCreate(['name' => 'view assets']);
        Permission::firstOrCreate(['name' => 'create assets']);
        Permission::firstOrCreate(['name' => 'edit assets']);
        Permission::firstOrCreate(['name' => 'delete assets']);
        Permission::firstOrCreate(['name' => 'import assets']);
        Permission::firstOrCreate(['name' => 'export assets']);

        // Asset Status Permissions
        Permission::firstOrCreate(['name' => 'change asset status']);

        // Audit Permissions
        Permission::firstOrCreate(['name' => 'view audit logs']);

        // User Management Permissions
        Permission::firstOrCreate(['name' => 'view users']);
        Permission::firstOrCreate(['name' => 'create users']);
        Permission::firstOrCreate(['name' => 'edit users']);
        Permission::firstOrCreate(['name' => 'delete users']);
        Permission::firstOrCreate(['name' => 'manage roles']);

        // Dashboard Permissions
        Permission::firstOrCreate(['name' => 'view dashboard']);

        // Reports Permissions
        Permission::firstOrCreate(['name' => 'view reports']);
        Permission::firstOrCreate(['name' => 'generate reports']);

        // ========================================
        // CREATE ROLES & ASSIGN PERMISSIONS
        // ========================================

        // 1. Super Admin - Full access to everything
        $superAdmin = Role::firstOrCreate(['name' => 'Super Admin']);
        $superAdmin->givePermissionTo(Permission::all());

        // 2. Asset Manager - Manage assets but not users
        $assetManager = Role::firstOrCreate(['name' => 'Asset Manager']);
        $assetManager->givePermissionTo([
            'view assets',
            'create assets',
            'edit assets',
            'delete assets',
            'import assets',
            'export assets',
            'change asset status',
            'view dashboard',
            'view reports',
            'generate reports',
        ]);

        // 3. Auditor - Read-only + audit logs
        $auditor = Role::firstOrCreate(['name' => 'Auditor']);
        $auditor->givePermissionTo([
            'view assets',
            'view audit logs',
            'view dashboard',
            'view reports',
        ]);

        // 4. Finance - View assets + reports for financial tracking
        $finance = Role::firstOrCreate(['name' => 'Finance']);
        $finance->givePermissionTo([
            'view assets',
            'view dashboard',
            'view reports',
            'generate reports',
            'export assets',
        ]);

        // 5. Viewer - Read-only access
        $viewer = Role::firstOrCreate(['name' => 'Viewer']);
        $viewer->givePermissionTo([
            'view assets',
            'view dashboard',
        ]);

        // ========================================
        // ASSIGN SUPER ADMIN TO FIRST USER
        // ========================================
        $user = User::first();
        if ($user) {
            $user->assignRole('Super Admin');
        }
    }
}

