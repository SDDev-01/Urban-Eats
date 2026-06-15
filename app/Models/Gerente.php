<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Gerente extends Model
{
    protected $table = 'gerente';
    protected $primaryKey = 'CodigoGerente';
    public $timestamps = false;

    protected $fillable = [
        'CodigoUsuario'
    ];

    //cardinalidad

    public function usuario(){
        return $this->belongsTo(Usuario::class,'CodigoUsuario');
    }

    public function restaurante(){
        return $this->hasMany(Restaurante::class,'CodigoGerente');
    }
}
