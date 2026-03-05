<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class TempAsset extends Model
{
    protected $fillable = [
        'import_batch_id',
        'cab_number',
        'project_code',
        'project_description',
        'asset_number',
        'asset_type_name',
        'supplier_name',
        'supplier_code',
        'article_code',
        'designation',
        'description',
        'serial_number',
        'order_number',
        'section_analytique',
        'zone',
        'localisation',
        'location_code',
        'quantity',
        'unit_price',
        'delivery_date',
        'service_start_date',
        'exit_date',
        'status',
        'raw_data',
        'validation_status',
        'validation_errors',
    ];

    /**
     * The attributes that should be cast.
     */
    protected $casts = [
        'quantity' => 'integer',
        'unit_price' => 'decimal:2',
        'delivery_date' => 'date',
        'service_start_date' => 'date',
        'exit_date' => 'date',
        'raw_data' => 'array',
    ];
}
