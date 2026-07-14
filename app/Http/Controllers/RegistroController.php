<?php

namespace App\Http\Controllers;

use App\Models\Direccion;
use App\Models\Telefono;
use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class RegistroController extends Controller
{
    public function mostrarPagina()
    {
        return view('registro');
    }

    public function Registrarse(Request $request)
    {
        // validacion interna
        $request->validate([
            'Nombres' => ['required', 'regex:/^[\pL\s\-\'\.]+$/u'],
            'Apellidos' => ['required', 'regex:/^[\pL\s\-\'\.]+$/u'],
            'Correo' => 'required|email|unique:usuario,Correo',
            'Telefono' => 'required',
            'Password' => 'required|min:8',
            'Direccion' => 'required',
        ]);

        $nombres = $request->input('Nombres');
        $apellidos = $request->input('Apellidos');
        $correo = $request->input('Correo');
        $telefono = $request->input('Telefono');
        $password = Hash::make($request->input('Password'));
        $direccion = $request->input('Direccion');

        $usuario = Usuario::create([
            'Correo' => $correo,
            'Password' => $password,
            'Nombres' => $nombres,
            'Apellidos' => $apellidos,
        ]);

        $codigoUsuario = $usuario->CodigoUsuario;

        Telefono::create([
            'Telefono' => $telefono,
            'CodigoUsuario' => $codigoUsuario,
        ]);

        Direccion::create([
            'Direccion' => $direccion,
            'CodigoUsuario' => $codigoUsuario,
        ]);

        session([
            'CodigoUsuario' => $usuario->CodigoUsuario,
            'Nombres' => $usuario->Nombres,
        ]);

        return redirect('/catalogo');
    }
}
