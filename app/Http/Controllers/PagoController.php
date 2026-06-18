<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Pago;

class PagoController extends Controller
{
    public function mostrarPagina(){
        return view('pago');
    }
    Public function iniciarPago(Request $request){
        //insertar en pago
        $pago = Pago::create([
            
        ]);
        //crear variables necesarias para la api

        //referenceCode sera Pago-[CodigoPago]
    }
    public function procesarWebhook(){

    }
    public function retornarDatos(){

    }
}
