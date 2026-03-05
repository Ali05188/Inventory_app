<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('assets', function (Blueprint $table) {
            $table->foreignId('supplier_id')->nullable()->after('delivery_date')->constrained('suppliers')->onDelete('set null');
            $table->foreignId('project_id')->nullable()->after('supplier_id')->constrained('projects')->onDelete('set null');
            $table->foreignId('asset_type_id')->nullable()->after('project_id')->constrained('asset_types')->onDelete('set null');
            $table->foreignId('location_id')->nullable()->after('asset_type_id')->constrained('locations')->onDelete('set null');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('assets', function (Blueprint $table) {
            $table->dropForeign(['supplier_id']);
            $table->dropForeign(['project_id']);
            $table->dropForeign(['asset_type_id']);
            $table->dropForeign(['location_id']);

            $table->dropColumn(['supplier_id', 'project_id', 'asset_type_id', 'location_id']);
        });
    }
};

