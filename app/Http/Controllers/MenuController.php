<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use App\Models\Menu;
use App\Models\Plato;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

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

        $restaurante = $gerente->restaurante()->first();
        $platosExistentes = Plato::whereNull('CodigoMenu')->orderBy('Nombre')->get();

        return view('menu', compact('restaurante', 'platosExistentes'));
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

        $restaurante = $gerente->restaurante()->first();

        DB::transaction(function () use ($request, $restaurante) {
            $menu = Menu::create([
                'Categoria' => $request->input('Categoria'),
                'CodigoRestaurante' => $restaurante->CodigoRestaurante,
            ]);

            $platosSeleccionados = array_filter($request->input('platos_existentes', []));
            if (! empty($platosSeleccionados)) {
                Plato::whereIn('CodigoPlato', $platosSeleccionados)
                    ->update(['CodigoMenu' => $menu->CodigoMenu]);
            }

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
        });

        return redirect('/perfilRestaurante')->with('exito', '¡Menú creado exitosamente!');
    }
}
