<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Plato extends Model
{
    protected $table = 'plato';
    protected $primaryKey = 'CodigoPlato';
    public $timestamps = false;

    protected $fillable = [
        'Nombre',
        'Descripcion',
        'Precio',
        'TipoComida',
        'Disponibilidad'
    ];

    //cardinalidad

    public function alergenos(){
        return $this->hasMany(Alergeno::class,'CodigoPlato');
    }

    public function opinion(){
        return $this->hasMany(Opiniones::class,'CodigoPlato');
    }

    public function menu(){
        return $this->belongsToMany(
            Menu::class,
            'plato_menu',
            'CodigoPlato',
            'CodigoMenu'
        );
    }
}
