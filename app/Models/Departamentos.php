<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Departamentos extends Model
{
    protected $table = 'departamento';
    protected $primaryKey = 'CodigoDepartamento';
    public $timestamps = false;

    protected $fillable = [
        'Nombre'
    ];

    //cardinalidad

    public function ciudad(){
        return $this->hasMany(Ciudades::class,'CodigoDepartamento');
    }
}
