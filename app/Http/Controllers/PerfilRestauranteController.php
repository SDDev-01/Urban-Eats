<?php

namespace App\Http\Controllers;

use App\Models\Gerente;

class PerfilRestauranteController extends Controller
{
    public function mostrarPerfil()
    {
        $codigoUsuario = session('CodigoUsuario');

        if (! $codigoUsuario) {
            return redirect('/login');
        }

        $gerente = Gerente::where('CodigoUsuario', $codigoUsuario)->first();

        // Si no tiene restaurante aún, redirigir al formulario de creación
        if (! $gerente) {
            return redirect('/restaurante')->with('info', 'Primero crea tu restaurante.');
        }

        $restaurante = $gerente->restaurante()->first();

        return view('perfilRestaurante', compact('restaurante'));
    }
}
