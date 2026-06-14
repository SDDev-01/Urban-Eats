<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Direccion extends Model
{
    protected $table = 'direccion';
    protected $primaryKey = 'CodigoDireccion';
    public $timestamps = false;

    protected $fillable = [
        'Direccion',
        'CodigoUsuario'
    ];

    //cardinalidad

    public function usuario(){
        return $this->belongsTo(Usuario::class,'CodigoUsuario');
    }
}
