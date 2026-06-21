<?php

namespace App\Http\Controllers;

use App\Models\DetallePedido;
use App\Models\Envio;
use App\Models\Pago;
use App\Models\Plato;
use App\Models\Transaccion;
use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use MercadoPago\Client\Common\RequestOptions;
use MercadoPago\Client\Payment\PaymentClient;
use MercadoPago\MercadoPagoConfig;

class PagoController extends Controller
{
    public function mostrarPagina()
    {
        return view('pago');
    }

    public function iniciarPago(Request $request)
    {
        $CodigoUsuario = session('CodigoUsuario');
        $Usuario = Usuario::find($CodigoUsuario);

        if (! $Usuario || ! $Usuario->cliente) {
            return response()->json(['error' => 'Sesión inválida'], 401);
        }

        $CodigoCliente = $Usuario->cliente->CodigoCliente;
        $Monto = (float) $request->input('transaction_amount');
        $items = $request->input('items', []);

        $pago = Pago::create([
            'CodigoCliente' => $CodigoCliente,
            'CodigoEnvio' => null,
            'Monto' => $Monto,
            'EstadoPago' => 'pending',
            'FechaPago' => now()->toDateString(),
            'HoraPago' => now()->toTimeString(),
        ]);

        $accessToken = config('services.mercadopago.access_token');
        MercadoPagoConfig::setAccessToken($accessToken);

        $client = new PaymentClient;
        $requestOptions = new RequestOptions;
        $requestOptions->setCustomHeaders(['X-Idempotency-Key: '.Str::uuid()->toString()]);

        $paymentData = array_merge($request->except('items'), ['description' => 'pedido Urban Eats']);
        $payment = $client->create($paymentData, $requestOptions);

        $pago->update(['EstadoPago' => $payment->status]);

        Transaccion::create([
            'TransaccionID' => (string) $payment->id,
            'CodigoPago' => $pago->CodigoPago,
            'MetodoPago' => $payment->payment_method_id,
            'BancoNombre' => null,
            'CUS' => null,
            'CodigoRespuesta' => $payment->status_detail,
        ]);

        $statusesExitosos = ['approved', 'in_process', 'authorized'];

        if (in_array($payment->status, $statusesExitosos) && ! empty($items)) {
            $codigoRestaurante = $items[0]['restaurante_id'] ?? null;

            if (! $codigoRestaurante) {
                $plato = Plato::with('menu')->find($items[0]['id'] ?? null);
                $codigoRestaurante = $plato?->menu?->CodigoRestaurante;
            }

            if ($codigoRestaurante) {
                DB::transaction(function () use ($CodigoCliente, $codigoRestaurante, $items, $pago) {
                    $envio = Envio::create([
                        'CodigoCliente' => $CodigoCliente,
                        'CodigoRepartidor' => 1,
                        'CodigoRestaurante' => $codigoRestaurante,
                        'Descripcion' => 'Pedido Urban Eats',
                        'FechaEnvio' => now()->toDateString(),
                        'HoraEntrega' => now()->addMinutes(35)->format('H:i:s'),
                    ]);

                    $pedido = $envio->pedido()->firstOrFail();

                    foreach ($items as $item) {
                        DetallePedido::create([
                            'CodigoPedido' => $pedido->CodigoPedido,
                            'CodigoPlato' => $item['id'],
                            'Cantidad' => (int) ($item['cantidad'] ?? 1),
                            'PrecioUnitario' => (float) ($item['precio'] ?? 0),
                        ]);
                    }

                    $pago->update(['CodigoEnvio' => $envio->CodigoEnvio]);

                    session(['pedido_activo' => $pedido->CodigoPedido]);
                });
            }
        }

        $mensajesError = [
            'cc_rejected_insufficient_amount' => 'Saldo insuficiente en la tarjeta.',
            'cc_rejected_bad_filled_card_number' => 'Número de tarjeta incorrecto.',
            'cc_rejected_bad_filled_date' => 'Fecha de vencimiento incorrecta.',
            'cc_rejected_bad_filled_other' => 'Datos de la tarjeta incorrectos.',
            'cc_rejected_high_risk' => 'Tu pago fue rechazado por seguridad.',
            'rejected_by_bank' => 'Pago rechazado por el banco.',
        ];

        return response()->json([
            'status' => $payment->status,
            'id' => $payment->id,
            'mensaje_error' => $mensajesError[$payment->status_detail ?? ''] ?? null,
        ]);
    }

    public function procesarWebhook() {}

    public function retornarDatos() {}
}
