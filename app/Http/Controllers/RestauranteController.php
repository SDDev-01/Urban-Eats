<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use App\Models\Restaurante;
use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\View\View;

class RestauranteController extends Controller
{
    public function listar(): View
    {
        $restaurantesJS = Restaurante::all()->map(fn ($r) => [
            'id' => $r->CodigoRestaurante,
            'nombre' => $r->Nombre,
            'direccion' => $r->Direccion ?? '',
            'horario' => $r->Horario ?? '',
        ]);

        return view('restaurantes', compact('restaurantesJS'));
    }

    public function mostrarPagina()
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        return view('restaurante');
    }

    public function crearRestaurante(Request $request)
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $codigoUsuario = session('CodigoUsuario');

        DB::transaction(function () use ($request, $codigoUsuario) {
            // 1. Crear el Gerente primero (requerido como FK del restaurante)
            $gerente = Gerente::firstOrCreate(['CodigoUsuario' => $codigoUsuario]);

            // 2. Crear el restaurante — es la acción principal del formulario
            Restaurante::create([
                'CodigoCiudad' => 1,
                'CodigoGerente' => $gerente->CodigoGerente,
                'Nombre' => $request->input('Nombre'),
                'Direccion' => $request->input('Direccion'),
                'Horario' => $request->input('Horario'),
            ]);

            // 3. Si el restaurante se creó bien, asignar rol Gerente (CodigoRol = 4)
            $usuario = Usuario::find($codigoUsuario);
            if (! $usuario->rol()->wherePivot('CodigoRol', 4)->exists()) {
                $usuario->rol()->attach(4);
            }
        });

        return redirect('/perfilRestaurante')->with('exito', '¡Restaurante creado exitosamente!');
    }
}
