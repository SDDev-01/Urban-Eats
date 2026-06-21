<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Str;
use App\Models\Pago;
use App\Models\Cliente;
use App\Models\Usuario;
use App\Models\Envio;
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
            'EstadoPago' => 'pending',
            'FechaPago' => now()->toDateString(),
            'HoraPago' => now()->toTimeString(),
        ]);
        //tarjeta
        $accessToken = config('services.mercadopago.access_token');
        MercadoPagoConfig::setAccessToken("$accessToken");

        $client = new PaymentClient();
        $request_options = new RequestOptions();
        $request_options->setCustomHeaders(["X-Idempotency-Key" => Str::uuid()->toString()]);
        
        //datos del brick mercado pago
        $data = $request->all();

        $payment = $client->create([
        "transaction_amount" => (float) $data['transaction_amount'],
        "token" => $data['token'],
        "description" => "pedido Urban Eats",
        "installments" => $data['installments'],
        "payment_method_id" => $data['payment_method_id'],
        "issuer_id" => $data['issuer_id'],
        "payer" => [
            "email" => $data['payer']['email'],
            "identification" => [
            "type" => $data['payer']['identification']['type'],
            "number" => $data['payer']['identification']['number']
            ]
        ]
        ], $request_options);
        return response()->json([
            "status" => $payment->status,
            "id" => $payment->id
        ]);
    }
    public function procesarWebhook(){

    }
    public function retornarDatos(){

    }
}
