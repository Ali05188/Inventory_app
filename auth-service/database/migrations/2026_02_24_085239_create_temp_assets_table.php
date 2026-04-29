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
        Schema::create('temp_assets', function (Blueprint $table) {
            $table->id();
            $table->foreignId('import_batch_id')->constrained('asset_import_batches')->onDelete('cascade');
            $table->string('cab_number')->nullable();
            $table->string('project_code')->nullable();
            $table->string('asset_number')->nullable();
            $table->string('supplier_code')->nullable();
            $table->string('location_code')->nullable();
            $table->string('designation')->nullable();
            $table->string('serial_number')->nullable();
            $table->integer('quantity')->nullable();
            $table->decimal('unit_price', 15, 2)->nullable();
            $table->date('delivery_date')->nullable();
            $table->json('raw_data')->nullable();
            $table->enum('validation_status', ['pending', 'valid', 'invalid'])->default('pending');
            $table->text('validation_errors')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('temp_assets');
    }
};
