<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class DetallePedido extends Model
{
    protected $table = 'detalle_pedido';

    protected $primaryKey = 'CodigoDetalle';

    public $timestamps = false;

    protected $fillable = [
        'CodigoPedido',
        'CodigoPlato',
        'Cantidad',
        'PrecioUnitario',
    ];

    public function pedido()
    {
        return $this->belongsTo(Pedido::class, 'CodigoPedido');
    }

    public function plato()
    {
        return $this->belongsTo(Plato::class, 'CodigoPlato');
    }
}
