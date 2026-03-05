<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;

class Asset extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'cab_number',
        'project_code',
        'asset_number',
        'designation',
        'serial_number',
        'quantity',
        'unit_price',
        'delivery_date',
        'status',
        'service_start_date',
        'exit_date',
        'exit_reason',
        'supplier_id',
        'project_id',
        'asset_type_id',
        'location_id',
    ];

    /**
     * The attributes that should be cast.
     */
    protected $casts = [
        'delivery_date' => 'date',
        'service_start_date' => 'date',
        'exit_date' => 'date',
        'unit_price' => 'decimal:2',
        'quantity' => 'integer',
    ];

    /**
     * Get the supplier that owns the asset.
     */
    public function supplier(): BelongsTo
    {
        return $this->belongsTo(Supplier::class);
    }

    /**
     * Get the project that owns the asset.
     */
    public function project(): BelongsTo
    {
        return $this->belongsTo(Project::class);
    }

    /**
     * Get the asset type.
     */
    public function assetType(): BelongsTo
    {
        return $this->belongsTo(AssetType::class);
    }

    /**
     * Get the location of the asset.
     */
    public function location(): BelongsTo
    {
        return $this->belongsTo(Location::class);
    }

    /**
     * Get the status history of the asset.
     */
    public function statusHistories(): HasMany
    {
        return $this->hasMany(AssetStatusHistory::class)->orderBy('created_at', 'desc');
    }
}
