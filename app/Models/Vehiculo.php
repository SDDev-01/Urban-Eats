<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Vehiculo extends Model
{
    protected $table = 'vehiculo';
    protected $primaryKey = 'Placa';
    public $timestamps = false;

    protected $fillable = [
        'CodigoRepartidor',
        'Licencia',
        'TipoVehiculo',
        'SeguroVehiculo',
        'SOAT'
    ];

    //cordinalidad

    public function repartidor(){
        return $this->belongsTo(Repartidor::class,'CodigoRepartidor');
    }

    
}
