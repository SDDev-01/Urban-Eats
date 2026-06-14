<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Pago extends Model
{
    protected $table = 'pago';
    protected $primaryKey = 'CodigoPago';
    public $timestamps = false;

    protected $fillable = [
        'CodigoCliente',
        'CodigoEnvio',
        'Monto',
        'FechaPago',
        'HoraPago',
        'EstadoPago'
    ];

    //coordinalidad

    public function transaccion(){
        return $this->hasOne(Transaccion::class,'CodigoPago');
    }

    public function cliente(){
        return $this->belongsTo(Cliente::class,'CodigoCliente');
    }

    public function envio(){
        return $this->belongsTo(Envio::class,'CodigoEnvio');
    }
}
