<?php

namespace App\Http\Controllers;

use App\Models\Direccion;
use App\Models\Telefono;
use App\Models\Usuario;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\View;

class PerfilController extends Controller
{
    public function MostrarDatos(): View|RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $usuario = Usuario::with(['telefono', 'direccion', 'gerente.restaurante', 'repartidor'])->find(session('CodigoUsuario'));

        $tieneRestaurante = $usuario->gerente?->restaurante->isNotEmpty() ?? false;
        $esRepartidor = $usuario->repartidor !== null;

        return view('perfil', compact('usuario', 'tieneRestaurante', 'esRepartidor'));
    }

    public function actualizar(Request $request): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $request->validate([
            'Nombres' => ['required', 'string', 'max:100'],
            'Apellidos' => ['required', 'string', 'max:100'],
            'Correo' => ['required', 'email', 'max:150'],
            'Telefono' => ['nullable', 'digits:10'],
            'Direccion' => ['nullable', 'string', 'max:255'],
        ]);

        $usuario = Usuario::find(session('CodigoUsuario'));

        $usuario->update([
            'Nombres' => $request->input('Nombres'),
            'Apellidos' => $request->input('Apellidos'),
            'Correo' => $request->input('Correo'),
        ]);

        if ($request->filled('Telefono')) {
            Telefono::updateOrCreate(
                ['CodigoUsuario' => $usuario->CodigoUsuario],
                ['Telefono' => $request->input('Telefono')]
            );
        }

        if ($request->filled('Direccion')) {
            Direccion::updateOrCreate(
                ['CodigoUsuario' => $usuario->CodigoUsuario],
                ['Direccion' => $request->input('Direccion')]
            );
        }

        session(['Nombres' => $usuario->Nombres]);

        return redirect('/perfil')->with('exito', 'Información actualizada correctamente.');
    }
}
