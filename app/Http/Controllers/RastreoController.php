<?php

namespace App\Http\Controllers;

use App\Models\Pedido;
use Illuminate\View\View;

class RastreoController extends Controller
{
    public function mostrar(): View
    {
        $codigoPedido = session('pedido_activo');
        $pedidoData = null;

        if ($codigoPedido) {
            $pedido = Pedido::with(['detalles.plato', 'restaurante'])->find($codigoPedido);

            if ($pedido) {
                $pedidoData = [
                    'codigo' => $pedido->CodigoPedido,
                    'estado' => $pedido->Estado,
                    'fecha' => $pedido->FechaPedido,
                    'restaurante' => $pedido->restaurante?->Nombre ?? '',
                    'items' => $pedido->detalles->map(fn ($d) => [
                        'nombre' => $d->plato?->Nombre ?? '',
                        'descripcion' => $d->plato?->Descripcion ?? '',
                        'cantidad' => $d->Cantidad,
                        'precio_unitario' => (float) $d->PrecioUnitario,
                        'subtotal' => $d->Cantidad * (float) $d->PrecioUnitario,
                    ])->values()->toArray(),
                    'total' => $pedido->detalles->sum(fn ($d) => $d->Cantidad * (float) $d->PrecioUnitario),
                ];
            }
        }

        return view('rastreo', compact('pedidoData'));
    }
}
