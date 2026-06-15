<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Usuario extends Model
{
    protected $table = 'usuario';
    protected $primaryKey = 'CodigoUsuario';

    public $timestamps = false;

    protected $fillable = [
        'Correo',
        'Password',
        'Nombres',
        'Apellidos'
    ];

    //cardinalidad

    public function rol(){
        return $this->belongsToMany(
            Rol::class,
            'rol_usuario',
            'CodigoUsuario',
            'CodigoRol'
        );
    }

    public function direccion(){
        return $this->hasMany(Direccion::class,'CodigoUsuario');
    }

    public function telefono(){
        return $this->hasMany(Telefono::class,'CodigoUsuario');
    }

    public function gerente(){
        return $this->hasOne(Gerente::class,'CodigoUsuario');
    }

    public function repartidor(){
        return $this->hasOne(Repartidor::class,'CodigoUsuario');
    }

    public function cliente(){
        return $this->hasOne(Cliente::class,'CodigoUsuario');
    }
}
