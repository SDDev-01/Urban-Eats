<?php

namespace App\Http\Controllers;

use App\Models\Cliente;
use App\Models\DetallePedido;
use App\Models\Envio;
use App\Models\Pago;
use App\Models\Plato;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class PedidoController extends Controller
{
    public function confirmar(Request $request): JsonResponse
    {
        if (! session('CodigoUsuario')) {
            return response()->json(['error' => 'No autenticado', 'redirect' => '/login'], 401);
        }

        $items = $request->input('items', []);
        $metodoPago = $request->input('metodo_pago', 'Tarjeta');

        if (empty($items)) {
            return response()->json(['error' => 'El carrito está vacío.'], 422);
        }

        $cliente = Cliente::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $cliente) {
            return response()->json(['error' => 'No se encontró el perfil de cliente.'], 422);
        }

        $codigoRestaurante = $items[0]['restaurante_id'] ?? null;

        if (! $codigoRestaurante) {
            $plato = Plato::with('menu')->find($items[0]['id'] ?? null);
            $codigoRestaurante = $plato?->menu?->CodigoRestaurante;
        }

        if (! $codigoRestaurante) {
            return response()->json(['error' => 'No se pudo determinar el restaurante.'], 422);
        }

        $total = collect($items)->sum(fn ($item) => ($item['precio'] ?? 0) * ($item['cantidad'] ?? 1));

        try {
            $codigoPedido = null;

            DB::transaction(function () use ($cliente, $codigoRestaurante, $items, $total, &$codigoPedido) {
                $envio = Envio::create([
                    'CodigoCliente' => $cliente->CodigoCliente,
                    'CodigoRepartidor' => 1,
                    'CodigoRestaurante' => $codigoRestaurante,
                    'Descripcion' => 'Pedido web',
                    'FechaEnvio' => now()->toDateString(),
                    'HoraEntrega' => now()->addMinutes(35)->format('H:i:s'),
                ]);

                $pedido = $envio->pedido()->firstOrFail();
                $codigoPedido = $pedido->CodigoPedido;

                foreach ($items as $item) {
                    DetallePedido::create([
                        'CodigoPedido' => $pedido->CodigoPedido,
                        'CodigoPlato' => $item['id'],
                        'Cantidad' => (int) ($item['cantidad'] ?? 1),
                        'PrecioUnitario' => (float) ($item['precio'] ?? 0),
                    ]);
                }

                Pago::create([
                    'CodigoCliente' => $cliente->CodigoCliente,
                    'CodigoEnvio' => $envio->CodigoEnvio,
                    'Monto' => $total,
                    'FechaPago' => now()->toDateString(),
                    'HoraPago' => now()->format('H:i:s'),
                    'EstadoPago' => 'Aceptado',
                ]);

                session(['pedido_activo' => $codigoPedido]);
            });

            return response()->json(['redirect' => '/rastreo']);
        } catch (\Throwable $e) {
            return response()->json(['error' => 'Error al procesar el pedido. Inténtalo nuevamente.'], 500);
        }
    }
}
