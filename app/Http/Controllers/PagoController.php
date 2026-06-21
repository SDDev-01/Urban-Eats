<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Pago;
use App\Models\Cliente;
use App\Models\Usuario;
use MercadoPago\Client\Payment\PaymentClient;
use MercadoPago\Client\Common\RequestOptions;
use MercadoPago\MercadoPagoConfig;

class PagoController extends Controller
{
    public function mostrarPagina(){
        return view('pago');
    }
    Public function iniciarPago(Request $request){
        dd($request->all());
        //codigocliente
        $CodigoUsuario = session('CodigoUsuario');
        $Usuario = Usuario::find($CodigoUsuario);
        
        $CodigoCliente = $Usuario->cliente->CodigoCliente;
        $Monto = $request->input('monto');
        $Envio = Envio::where('CodigoCliente', $CodigoCliente) -> latest('FechaEnvio')->first();
        //insertar en pago
        $pago = Pago::create([
            'CodigoCliente' => $CodigoCliente,
            'CodigoEnvio' => $Envio?->CodigoEnvio,
            'Monto' => $Monto,
            'FechaPago' => now()->toDateString(),
            'HoraPago' => now()->toTimeString()
        ]);
        //tarjeta
        $accessToken = config('services.mercadopago.access_token');
        MercadoPagoConfig::setAccessToken("$accessToken");

        $client = new PaymentClient();
        $request_options = new RequestOptions();
        $request_options->setCustomHeaders(["X-Idempotency-Key: <SOME_UNIQUE_VALUE>"]);
        
        //datos del form
        $data = $request->all();

        $payment = $client->create([
        "transaction_amount" => (float) $_POST['<TRANSACTION_AMOUNT>'],
        "token" => $_POST['<TOKEN>'],
        "description" => $_POST['<DESCRIPTION>'],
        "installments" => $_POST['<INSTALLMENTS>'],
        "payment_method_id" => $_POST['<PAYMENT_METHOD_ID'],
        "issuer_id" => $_POST['<ISSUER>'],
        "payer" => [
            "email" => $_POST['<EMAIL>'],
            "identification" => [
            "type" => $_POST['<IDENTIFICATION_TYPE'],
            "number" => $_POST['<NUMBER>']
            ]
        ]
        ], $request_options);
        echo implode($payment);
        //referenceCode sera Pago-[CodigoPago]
    }
    public function procesarWebhook(){

    }
    public function retornarDatos(){

    }
}
