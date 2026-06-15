<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Hash;
use App\Models\Usuario;

class PerfilController extends Controller
{
    public function MostrarDatos(){
        $codigoUsuario = session('CodigoUsuario');
        $usuario = Usuario::find($codigoUsuario);

        //extraemos datos
        $datosUsuario = compact('usuario');

        return view('perfil', $datosUsuario);
    }
}
