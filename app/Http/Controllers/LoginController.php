<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use App\Models\Repartidor;
use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class LoginController extends Controller
{
    public function mostrarPagina()
    {
        return view('login');
    }

    public function iniciarSesion(Request $request)
    {
        $correo = $request->input('email');
        $password = $request->input('password');

        $usuario = Usuario::where('Correo', $correo)->first();

        if ($usuario && Hash::check($password, $usuario->Password)) {
            session([
                'CodigoUsuario' => $usuario->CodigoUsuario,
                'Nombres' => $usuario->Nombres,
            ]);

            $esGerente = Gerente::where('CodigoUsuario', $usuario->CodigoUsuario)->exists();

            if ($esGerente) {
                return redirect('/seleccion-rol');
            }

            $esRepartidor = Repartidor::where('CodigoUsuario', $usuario->CodigoUsuario)->exists();

            return $esRepartidor ? redirect('/perfilRepartidor') : redirect('/catalogo');
        } else {
            // salio mal
            return back()->with('error', 'Correo o contraseña incorrectos');
        }
    }
}
