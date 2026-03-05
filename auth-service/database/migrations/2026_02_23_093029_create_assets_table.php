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
        Schema::create('assets', function (Blueprint $table) {
            $table->id();
            $table->string('cab_number')->nullable();
            $table->string('project_code')->nullable();
            $table->string('asset_number')->nullable();
            $table->string('designation')->nullable();
            $table->string('serial_number')->nullable();
            $table->integer('quantity')->nullable();
            $table->decimal('unit_price', 15, 2)->nullable();
            $table->date('delivery_date')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('assets');
    }
};
