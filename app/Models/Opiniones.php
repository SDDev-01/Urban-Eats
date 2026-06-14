<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Opiniones extends Model
{
    protected $table = 'opinion';
    protected $primaryKey = 'CodigoOpinion';
    public $timestamps = false;

    protected $fillable = [
        'CodigoPlato',
        'CodigoCliente',
        'CodigoRepartidor',
        'Opinion',
        'Fecha'
    ];

    //cardinalidad

    public function repartidor(){
        return $this->belongsTo(Repatridor::class,'CodigoRepartidor');
    }
    
    public function cliente(){
        return $this->belongsTo(Cliente::class,'CodigoCliente');
    }

    public function plato(){
        return $this->belongsTo(Plato::class,'CodigoPlato');
    }

}
