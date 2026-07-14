<?php

namespace App\Http\Controllers;

use App\Models\Restaurante;
use Illuminate\View\View;

class CatalogoController extends Controller
{
    public function mostrar(): View
    {
        $productosJS = Restaurante::with(['menu.plato'])->get()
            ->flatMap(function ($restaurante) {
                return $restaurante->menu->flatMap(function ($menu) use ($restaurante) {
                    return $menu->plato->map(fn ($p) => [
                        'id' => $p->CodigoPlato,
                        'nombre' => $p->Nombre,
                        'categoria' => $p->TipoComida ?? $menu->Categoria,
                        'descripcion' => $p->Descripcion ?? '',
                        'precio' => (float) ($p->Precio ?? 0),
                        'restaurante_id' => $restaurante->CodigoRestaurante,
                        'restauranteNombre' => $restaurante->Nombre,
                    ]);
                });
            })->values();

        return view('catalogo', compact('productosJS'));
    }
}
