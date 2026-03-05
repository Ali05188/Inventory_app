<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Location extends Model
{
    protected $fillable = [
        'code',
        'name',
        'building',
        'floor',
        'room',
        'description',
        'is_active',
    ];
}
