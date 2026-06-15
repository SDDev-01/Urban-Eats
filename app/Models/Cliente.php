<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Cliente extends Model
{
    protected $table = 'cliente';
    protected $primaryKey = 'CodigoCliente';
    public $timestamps = false;

    protected $fillable = [
        'CodigoUsuario'
    ];

    //cardinalidad

    public function usuario(){
        return $this->belongsTo(Usuario::class,'CodigoUsuario');
    }

    public function pago(){
        return $this->hasMany(Pago::class,'CodigoCliente');
    }

    public function envio(){
        return $this->hasMany(Envio::class,'CodigoCliente');
    }

    public function Opiniones(){
        return $this->hasMany(Opiniones::class,'CodigoCliente');
    }
}
