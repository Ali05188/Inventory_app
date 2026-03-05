<?php

namespace Database\Seeders;

use App\Models\Project;
use App\Models\Supplier;
use App\Models\Location;
use Illuminate\Database\Seeder;

class ReferenceDataSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create Projects
        $projects = [
            ['code' => 'PROJ001', 'name' => 'Project Alpha', 'status' => 'active'],
            ['code' => 'PROJ002', 'name' => 'Project Beta', 'status' => 'active'],
            ['code' => 'PROJ003', 'name' => 'Project Gamma', 'status' => 'active'],
        ];

        foreach ($projects as $project) {
            Project::firstOrCreate(['code' => $project['code']], $project);
        }

        // Create Suppliers
        $suppliers = [
            ['code' => 'SUP001', 'name' => 'Dell Technologies', 'email' => 'contact@dell.com', 'is_active' => true],
            ['code' => 'SUP002', 'name' => 'HP Inc.', 'email' => 'contact@hp.com', 'is_active' => true],
            ['code' => 'SUP003', 'name' => 'Lenovo', 'email' => 'contact@lenovo.com', 'is_active' => true],
        ];

        foreach ($suppliers as $supplier) {
            Supplier::firstOrCreate(['code' => $supplier['code']], $supplier);
        }

        // Create Locations
        $locations = [
            ['code' => 'LOC001', 'name' => 'Building A - Floor 1', 'building' => 'A', 'floor' => '1', 'is_active' => true],
            ['code' => 'LOC002', 'name' => 'Building A - Floor 2', 'building' => 'A', 'floor' => '2', 'is_active' => true],
            ['code' => 'LOC003', 'name' => 'Building B - Floor 1', 'building' => 'B', 'floor' => '1', 'is_active' => true],
        ];

        foreach ($locations as $location) {
            Location::firstOrCreate(['code' => $location['code']], $location);
        }
    }
}

