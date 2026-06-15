<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Pedido extends Model
{
    protected $table = 'pedido';
    protected $primaryKey = 'CodigoPedido';
    public $timestamps = false;

    protected $fillable = [
        'CodigoEnvio',
        'CodigoRestaurante',
        'FechaPedido',
        'Estado'
    ];

    //cardinalidad

    public function envio(){
        return $this->belongsTo(Envio::class,'CodigoEnvio');
    }

    public function restaurante(){
        return $this->belongsTo(Restaurante::class,'CodigoRestaurante');
    }
}
