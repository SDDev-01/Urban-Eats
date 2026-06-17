<?php

namespace App\Http\Controllers;

class LogoutController extends Controller
{
    public function logout()
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        session()->flush();

        return redirect('/login');
    }
}
