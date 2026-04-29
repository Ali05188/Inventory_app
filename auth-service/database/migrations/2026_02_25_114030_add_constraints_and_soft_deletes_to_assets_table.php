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
            // PART 6: Add soft deletes
            $table->softDeletes();

            // PART 4: Add unique constraint on serial_number
            $table->unique('serial_number');

            // PART 4: Add indexes for performance
            $table->index('project_id');
            $table->index('supplier_id');
            $table->index('cab_number');
            $table->index('asset_number');
            $table->index('location_id');
            $table->index('asset_type_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('assets', function (Blueprint $table) {
            $table->dropSoftDeletes();
            $table->dropUnique(['serial_number']);
            $table->dropIndex(['project_id']);
            $table->dropIndex(['supplier_id']);
            $table->dropIndex(['cab_number']);
            $table->dropIndex(['asset_number']);
            $table->dropIndex(['location_id']);
            $table->dropIndex(['asset_type_id']);
        });
    }
};
