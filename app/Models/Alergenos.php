<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Alergenos extends Model
{
    protected $table = 'alergeno';
    protected $primaryKey = 'CodigoAlergeno';
    public $timestamps = false;

    protected $fillable = [
        'CodigoPlato',
        'Nombre',
    ];

    //cardinalidad

    public function plato(){
        return $this->belongsTo(Plato::class,'CodigoPlato');
    }
}
