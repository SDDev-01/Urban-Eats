<?php

namespace App\Http\Controllers;

use App\Models\Restaurante;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\View;

class RestauranteDetalleController extends Controller
{
    public function mostrarDetalle(Request $request): RedirectResponse|View
    {
        $restaurante = Restaurante::with(['menu.plato'])->find($request->query('id'));

        if (! $restaurante) {
            return redirect('/restaurantes');
        }

        $restauranteJS = [
            'id' => $restaurante->CodigoRestaurante,
            'nombre' => $restaurante->Nombre,
            'direccion' => $restaurante->Direccion ?? '',
            'horario' => $restaurante->Horario ?? '',
        ];

        $productosJS = $restaurante->menu->flatMap(function ($menu) {
            return $menu->plato->map(fn ($p) => [
                'id' => $p->CodigoPlato,
                'nombre' => $p->Nombre,
                'categoria' => $p->TipoComida ?? $menu->Categoria,
                'descripcion' => $p->Descripcion ?? '',
                'precio' => (float) ($p->Precio ?? 0),
            ]);
        })->values();

        return view('restauranteDetalle', compact('restauranteJS', 'productosJS'));
    }
}
