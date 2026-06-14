<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Envio extends Model
{
    protected $table = 'envio';
    protected $primaryKey = 'CodigoEnvio';
    public $timestamps = false;

    protected $fillable = [
        'CodigoCliente',
        'CodigoRepartidor',
        'CodigoRestaurante',
        'Descripcion',
        'FechaEnvio',
        'HoraEntrega'
    ];

    //cardinalidad

    public function repartidor(){
        return $this->belongsTo(Repartidor::class,'CodigoRepartidor');
    }

    public function cliente(){
        return $this->belongsTo(Cliente::class,'CodigoCliente');
    }

    public function pago(){
        return $this->hasMany(Pago::class,'CodigoEnvio');
    }

    public function pedido(){
        return $this->hasOne(Pedido::class,'CodigoEnvio');
    }
}
