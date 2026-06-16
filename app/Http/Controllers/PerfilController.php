<?php

namespace App\Http\Controllers;

use App\Models\Usuario;

class PerfilController extends Controller
{
    public function MostrarDatos()
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $usuario = Usuario::find(session('CodigoUsuario'));

        return view('perfil', compact('usuario'));
    }
}
