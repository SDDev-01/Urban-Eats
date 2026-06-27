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
use Illuminate\Support\Facades\Log;
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
        if (app()->isLocal()) {
            MercadoPagoConfig::setRuntimeEnviroment(MercadoPagoConfig::LOCAL);
        }

        $client = new PaymentClient;
        $requestOptions = new RequestOptions;
        $requestOptions->setCustomHeaders(['X-Idempotency-Key: '.Str::uuid()->toString()]);

        $paymentData = array_merge($request->except('items'), ['description' => 'pedido Urban Eats']);

        $mpFallback = false;
        $paymentStatus = 'in_process';
        $paymentId = null;
        $paymentStatusDetail = null;

        try {
            $payment = $client->create($paymentData, $requestOptions);
            $paymentStatus = $payment->status;
            $paymentId = $payment->id;
            $paymentStatusDetail = $payment->status_detail;

            $pago->update(['EstadoPago' => $paymentStatus]);

            Transaccion::create([
                'TransaccionID' => (string) $payment->id,
                'CodigoPago' => $pago->CodigoPago,
                'MetodoPago' => $payment->payment_method_id,
                'BancoNombre' => null,
                'CUS' => null,
                'CodigoRespuesta' => $payment->status_detail,
            ]);
        } catch (\Exception $e) {
            $mpFallback = true;
            $pago->update(['EstadoPago' => 'in_process']);
        }

        $statusesExitosos = ['approved', 'in_process', 'authorized'];

        if (($mpFallback || in_array($paymentStatus, $statusesExitosos)) && ! empty($items)) {
            $codigoRestaurante = $items[0]['restaurante_id'] ?? null;

            if (! $codigoRestaurante) {
                $plato = Plato::with('menu')->find($items[0]['id'] ?? null);
                $codigoRestaurante = $plato?->menu?->CodigoRestaurante;
            }

            if ($codigoRestaurante) {
                try {
                    DB::transaction(function () use ($CodigoCliente, $codigoRestaurante, $items, $pago) {
                        $envio = Envio::create([
                            'CodigoCliente' => $CodigoCliente,
                            'CodigoRepartidor' => 1,
                            'CodigoRestaurante' => $codigoRestaurante,
                            'Descripcion' => 'Pedido Urban Eats',
                            'FechaEnvio' => now()->toDateString(),
                            'HoraEntrega' => now()->addMinutes(35)->format('H:i:s'),
                        ]);

                        $pedido = $envio->pedido()->create([
                            'CodigoRestaurante' => $codigoRestaurante,
                            'FechaPedido' => now()->toDateString(),
                            'Estado' => 'Iniciando',
                        ]);

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
                } catch (\Exception $e) {
                    Log::error('Error creando pedido tras pago: '.$e->getMessage(), [
                        'items' => $items,
                        'codigoRestaurante' => $codigoRestaurante,
                    ]);
                }
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
            'status' => $mpFallback ? 'in_process' : $paymentStatus,
            'id' => $paymentId,
            'mensaje_error' => $mensajesError[$paymentStatusDetail ?? ''] ?? null,
        ]);
    }

    public function procesarWebhook() {}

    public function retornarDatos() {}
}
