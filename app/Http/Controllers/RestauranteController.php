<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use App\Models\Restaurante;
use App\Models\Usuario;
use Illuminate\Http\Request;

class RestauranteController extends Controller
{
    public function mostrarPagina()
    {
        return view('restaurante');
    }

    public function crearRestaurante(Request $request)
    {
        $codigoUsuario = session('CodigoUsuario');

        if (! $codigoUsuario) {
            return redirect('/login');
        }

        // Crear o recuperar el registro de gerente para este usuario
        $gerente = Gerente::firstOrCreate(['CodigoUsuario' => $codigoUsuario]);

        // Asignar rol Gerente (CodigoRol = 4) si el usuario no lo tiene aún
        $usuario = Usuario::find($codigoUsuario);
        if (! $usuario->rol()->where('CodigoRol', 4)->exists()) {
            $usuario->rol()->attach(4);
        }

        // Crear el restaurante asociado al gerente (CodigoCiudad = 1 → Bogotá)
        Restaurante::create([
            'CodigoCiudad' => 1,
            'CodigoGerente' => $gerente->CodigoGerente,
            'Nombre' => $request->input('Nombre'),
            'Ubicacion' => $request->input('Ubicacion'),
            'Horario' => $request->input('Horario'),
        ]);

        return redirect('/perfilRestaurante')->with('exito', '¡Restaurante creado exitosamente!');
    }
}
