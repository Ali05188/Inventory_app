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
        Schema::table('asset_import_batches', function (Blueprint $table) {
            $table->integer('processed_rows')->default(0)->after('total_rows');
            $table->decimal('execution_time_seconds', 10, 2)->nullable()->after('failed_rows');
            $table->json('error_summary')->nullable()->after('execution_time_seconds');
            $table->timestamp('started_at')->nullable()->after('error_summary');
            $table->timestamp('completed_at')->nullable()->after('started_at');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('asset_import_batches', function (Blueprint $table) {
            $table->dropColumn([
                'processed_rows',
                'execution_time_seconds',
                'error_summary',
                'started_at',
                'completed_at',
            ]);
        });
    }
};
