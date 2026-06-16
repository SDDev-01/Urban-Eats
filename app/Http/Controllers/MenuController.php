<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use App\Models\Menu;
use App\Models\Plato;
use Illuminate\Http\Request;

class MenuController extends Controller
{
    public function mostrarFormulario()
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $gerente = Gerente::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $gerente) {
            return redirect('/restaurante')->with('info', 'Primero crea tu restaurante.');
        }

        $codigoActivo = session('restaurante_activo');
        $restaurante = $codigoActivo
            ? $gerente->restaurante()->where('CodigoRestaurante', $codigoActivo)->first()
            : $gerente->restaurante()->first();

        if (! $restaurante) {
            return redirect('/seleccion-restaurante');
        }

        return view('menu', compact('restaurante'));
    }

    public function crearMenu(Request $request)
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $gerente = Gerente::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $gerente) {
            return redirect('/restaurante');
        }

        $codigoActivo = session('restaurante_activo');
        $restaurante = $codigoActivo
            ? $gerente->restaurante()->where('CodigoRestaurante', $codigoActivo)->first()
            : $gerente->restaurante()->first();

        if (! $restaurante) {
            return redirect('/seleccion-restaurante');
        }

        $menu = Menu::create([
            'Categoria' => $request->input('Categoria'),
            'CodigoRestaurante' => $restaurante->CodigoRestaurante,
        ]);

        foreach ($request->input('nuevos_platos', []) as $platoData) {
            if (empty(trim($platoData['Nombre'] ?? ''))) {
                continue;
            }

            Plato::create([
                'CodigoMenu' => $menu->CodigoMenu,
                'Nombre' => $platoData['Nombre'],
                'Descripcion' => $platoData['Descripcion'] ?? null,
                'Precio' => $platoData['Precio'] ?? null,
                'TipoComida' => $platoData['TipoComida'] ?? null,
                'Disponibilidad' => $platoData['Disponibilidad'] ?? 'Disponible',
            ]);
        }

        return redirect('/perfilRestaurante')->with('exito', '¡Menú creado exitosamente!');
    }
}
