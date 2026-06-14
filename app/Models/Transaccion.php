<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Transaccion extends Model
{
    protected $table = 'transaccion';
    protected $primaryKey = 'TransaccionId';
    public $timestamps = false;

    protected $fillable = [
        'CodigoPago',
        'MetodoPago',
        'BancoNombre',
        'CUS',
        'CodigoRespuesta'
    ];

    //coordinalidad

    public function pago(){
        return $this->belongsTo(Pago::class,'CodigoPago');
    }
}
