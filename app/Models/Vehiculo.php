<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Vehiculo extends Model
{
    protected $table = 'vehiculo';

    protected $primaryKey = 'Placa';

    public $incrementing = false;

    protected $keyType = 'string';

    public $timestamps = false;

    protected $fillable = [
        'Placa',
        'CodigoRepartidor',
        'TipoVehiculo',
        'SeguroVehiculo',
        'SOAT',
    ];

    // cordinalidad

    public function repartidor()
    {
        return $this->belongsTo(Repartidor::class, 'CodigoRepartidor');
    }
}
