<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Transaccion extends Model
{
    protected $table = 'transaccion';

    protected $primaryKey = 'TransaccionID';

    protected $keyType = 'string';

    public $incrementing = false;

    public $timestamps = false;

    protected $fillable = [
        'TransaccionID',
        'CodigoPago',
        'MetodoPago',
        'BancoNombre',
        'CUS',
        'CodigoRespuesta',
    ];

    // coordinalidad

    public function pago()
    {
        return $this->belongsTo(Pago::class, 'CodigoPago');
    }
}
