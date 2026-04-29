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
            $table->date('service_start_date')->nullable()->after('status');
            $table->date('exit_date')->nullable()->after('service_start_date');
            $table->string('exit_reason')->nullable()->after('exit_date');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('assets', function (Blueprint $table) {
            $table->dropColumn(['service_start_date', 'exit_date', 'exit_reason']);
        });
    }
};

