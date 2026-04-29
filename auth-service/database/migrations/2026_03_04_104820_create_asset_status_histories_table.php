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
        Schema::create('asset_status_histories', function (Blueprint $table) {
            $table->id();

            // Foreign key to assets table with cascade delete
            $table->foreignId('asset_id')
                  ->constrained('assets')
                  ->onDelete('cascade');

            // Status tracking
            $table->string('previous_status')->nullable();
            $table->string('new_status');

            // Foreign key to users table (who made the change)
            $table->foreignId('changed_by')
                  ->nullable()
                  ->constrained('users')
                  ->onDelete('set null');

            // Reason for the change
            $table->string('reason')->nullable();

            // Optional metadata (JSON)
            $table->json('metadata')->nullable();

            $table->timestamps();

            // Indexes for performance
            $table->index('asset_id');
            $table->index('changed_by');
            $table->index('new_status');
            $table->index('created_at');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('asset_status_histories');
    }
};

