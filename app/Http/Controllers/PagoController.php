<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Pago;
use App\Models\Cliente;
use App\Models\Usuario;

class PagoController extends Controller
{
    public function mostrarPagina(){
        return view('pago');
    }
    Public function iniciarPago(Request $request){
        //codigocliente
        $CodigoUsuario = session('CodigoUsuario');
        $Usuario = Usuario::find($CodigoUsuario);
        
        $CodigoCliente = $Usuario->cliente->CodigoCliente;
        $Monto = $request->input('monto');
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
