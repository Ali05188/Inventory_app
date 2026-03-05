<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class AssetImportBatch extends Model
{
    protected $fillable = [
        'file_name',
        'total_rows',
        'processed_rows',
        'success_rows',
        'failed_rows',
        'status',
        'execution_time_seconds',
        'error_summary',
        'started_at',
        'completed_at',
        'created_by',
    ];

    protected $casts = [
        'error_summary' => 'array',
        'started_at' => 'datetime',
        'completed_at' => 'datetime',
        'execution_time_seconds' => 'decimal:2',
    ];

    /**
     * Status constants
     */
    public const STATUS_PENDING = 'pending';
    public const STATUS_PROCESSING = 'processing';
    public const STATUS_COMPLETED = 'completed';
    public const STATUS_FAILED = 'failed';

    /**
     * Get the user who created this batch
     */
    public function creator(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    /**
     * Get the temp assets for this batch
     */
    public function tempAssets(): HasMany
    {
        return $this->hasMany(TempAsset::class, 'import_batch_id');
    }

    /**
     * Check if batch is completed
     */
    public function isCompleted(): bool
    {
        return $this->status === self::STATUS_COMPLETED;
    }

    /**
     * Check if batch failed
     */
    public function isFailed(): bool
    {
        return $this->status === self::STATUS_FAILED;
    }
}
