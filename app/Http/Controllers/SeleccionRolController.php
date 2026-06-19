<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use Illuminate\Http\RedirectResponse;
use Illuminate\View\View;

class SeleccionRolController extends Controller
{
    public function mostrarRol(): View|RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $gerente = Gerente::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $gerente) {
            return redirect('/catalogo');
        }

        return view('seleccionRol');
    }

    public function mostrarRestaurantes(): View|RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $gerente = Gerente::with('restaurante')->where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $gerente) {
            return redirect('/catalogo');
        }

        $restaurantes = $gerente->restaurante;

        return view('seleccionRestaurante', compact('restaurantes'));
    }

    public function confirmarRestaurante(int $id): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $gerente = Gerente::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $gerente) {
            return redirect('/catalogo');
        }

        $restaurante = $gerente->restaurante()->where('CodigoRestaurante', $id)->firstOrFail();

        session(['restaurante_activo' => $restaurante->CodigoRestaurante]);

        return redirect('/perfilRestaurante');
    }
}
