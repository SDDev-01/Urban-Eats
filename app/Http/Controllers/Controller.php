<?php

namespace App\Http\Controllers;

use Illuminate\Http\RedirectResponse;

abstract class Controller
{
    /**
     * Verifica que haya una sesión activa.
     * Si no hay usuario logueado, devuelve un redirect a /login.
     * Uso: if ($redireccion = $this->requiereLogin()) return $redireccion;
     */
    protected function requiereLogin(): ?RedirectResponse
    {
        if (! session('CodigoUsuario')) {
            return redirect('/login');
        }

        return null;
    }
}
