<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Rol extends Model
{
    protected $table = 'rol';
    protected $primaryKey = 'CodigoRol';
    public $timestamps = false;

    protected $fillable = [
        'NombreRol',
        'DescripcionRol'
    ];

    //cardinalidad

    public function usuario(){
        return $this->belongsToMany(
            Usuario::class,
            'rol_usuario',
            'CodigoRol',
            'CodigoUsuario'
        );
    }
}
