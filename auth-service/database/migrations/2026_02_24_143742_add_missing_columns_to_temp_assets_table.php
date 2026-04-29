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
        Schema::table('temp_assets', function (Blueprint $table) {
            // Project related
            $table->string('project_description')->nullable()->after('project_code');

            // Asset type
            $table->string('asset_type_name')->nullable()->after('asset_number');

            // Supplier
            $table->string('supplier_name')->nullable()->after('asset_type_name');

            // Article
            $table->string('article_code')->nullable()->after('supplier_name');

            // Description
            $table->text('description')->nullable()->after('designation');

            // Order
            $table->string('order_number')->nullable()->after('serial_number');

            // Analytical section
            $table->string('section_analytique')->nullable()->after('order_number');

            // Zone and location
            $table->string('zone')->nullable()->after('section_analytique');
            $table->string('localisation')->nullable()->after('zone');

            // Dates
            $table->date('service_start_date')->nullable()->after('delivery_date');
            $table->date('exit_date')->nullable()->after('service_start_date');

            // Status
            $table->string('status')->nullable()->after('exit_date');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('temp_assets', function (Blueprint $table) {
            $table->dropColumn([
                'project_description',
                'asset_type_name',
                'supplier_name',
                'article_code',
                'description',
                'order_number',
                'section_analytique',
                'zone',
                'localisation',
                'service_start_date',
                'exit_date',
                'status',
            ]);
        });
    }
};
