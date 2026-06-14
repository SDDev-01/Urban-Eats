<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Ciudades extends Model
{
    protected $table = 'ciudad';
    protected $primaryKey = 'CodigoCiudad';
    public $timestamps = false;

    protected $fillable = [
        'CodigoDepartamento',
        'Nombre',
        'Latitud',
        'Longitud'
    ];

    //cardinalidad

    public function departamento(){
        return $this->belongsTo(Departamentos::class,'CodigoDepartamento');
    }

    public function restaurante(){
        return $this->hasMany(Restaurante::class,'CodigoCiudad');
    }
}
