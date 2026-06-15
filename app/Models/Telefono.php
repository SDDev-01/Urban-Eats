<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Telefono extends Model
{
    protected $table = 'telefono';
    protected $primaryKey = 'CodigoTelefono';
    public $timestamps = false;

    protected $fillable = [
        'Telefono',
        'CodigoUsuario'
    ];

    //cardinalidad
    
    public function usuario(){
        return $this->belongsTo(Usuario::class,'CodigoUsuario');
    }
}
