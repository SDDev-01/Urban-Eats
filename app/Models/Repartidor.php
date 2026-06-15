<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Repartidor extends Model
{
    protected $table = 'repartidor';
    protected $primaryKey = 'CodigoRepartidor';
    public $timestamps = false;

    protected $fillable = [
        'CodigoUsuario'
    ];

    //cardinalidad

    public function usuario(){
        return $this->belongsTo(Usuario::class,'CodigoUsuario');
    }

    public function envio(){
        return $this->hasMany(Envio::class,'CodigoRepartidor');
    }

    public function vehiculo(){
        return $this->hasMany(Vehiculo::class,'CodigoRepartidor');
    }

    public function opinion(){
        return $this->hasMany(Opiniones::class,'CodigoRepartidor');
    }
}
