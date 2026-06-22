<?php

namespace App\Http\Controllers;

use App\Models\Envio;
use App\Models\Repartidor;
use App\Models\Vehiculo;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\View;

class RepartidorController extends Controller
{
    public function mostrarFormulario(): View|RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $repartidor = Repartidor::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if ($repartidor) {
            return redirect('/perfilRepartidor');
        }

        return view('repartidor');
    }

    public function registrar(Request $request): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $existente = Repartidor::where('CodigoUsuario', session('CodigoUsuario'))->first();
        if ($existente) {
            return redirect('/perfilRepartidor');
        }

        $request->validate([
            'TipoVehiculo' => ['required', 'in:Moto,Carro,Bicicleta,Bici'],
            'Placa' => ['required', 'string', 'max:20', 'unique:vehiculo,Placa'],
            'SOAT' => ['nullable', 'string', 'max:100'],
            'SeguroVehiculo' => ['nullable', 'string', 'max:100'],
        ]);

        $repartidor = Repartidor::create([
            'CodigoUsuario' => session('CodigoUsuario'),
        ]);

        Vehiculo::create([
            'Placa' => strtoupper($request->input('Placa')),
            'CodigoRepartidor' => $repartidor->CodigoRepartidor,
            'TipoVehiculo' => $request->input('TipoVehiculo'),
            'SOAT' => $request->input('SOAT'),
            'SeguroVehiculo' => $request->input('SeguroVehiculo'),
        ]);

        return redirect('/perfilRepartidor')->with('exito', '¡Te has registrado como repartidor!');
    }

    public function mostrarPerfil(): View|RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $repartidor = Repartidor::with(['usuario', 'vehiculo'])
            ->where('CodigoUsuario', session('CodigoUsuario'))
            ->first();

        if (! $repartidor) {
            return redirect('/repartidor');
        }

        $misEnvios = Envio::with(['pedido.detalles.plato', 'pedido.restaurante'])
            ->where('CodigoRepartidor', $repartidor->CodigoRepartidor)
            ->whereHas('pedido')
            ->orderByDesc('FechaEnvio')
            ->get();

        $pedidosEnCurso = $misEnvios->filter(fn ($e) => $e->pedido?->Estado === 'En Proceso')->values();
        $pedidosRealizados = $misEnvios->filter(fn ($e) => $e->pedido?->Estado === 'Entregado')->values();

        $pedidosDisponibles = collect();
        if ($repartidor->CodigoRepartidor !== 1) {
            $pedidosDisponibles = Envio::with(['pedido.detalles.plato', 'pedido.restaurante'])
                ->whereHas('pedido', fn ($q) => $q->where('Estado', 'Iniciando'))
                ->orderByDesc('FechaEnvio')
                ->get();
        }

        return view('perfilRepartidor', compact('repartidor', 'pedidosEnCurso', 'pedidosRealizados', 'pedidosDisponibles'));
    }

    public function tomarPedido(int $envioId): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $repartidor = Repartidor::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $repartidor) {
            return redirect('/repartidor');
        }

        $envio = Envio::with('pedido')
            ->whereHas('pedido', fn ($q) => $q->where('Estado', 'Iniciando'))
            ->where('CodigoEnvio', $envioId)
            ->firstOrFail();

        $envio->update(['CodigoRepartidor' => $repartidor->CodigoRepartidor]);
        $envio->pedido->update(['Estado' => 'En Proceso']);

        return redirect('/perfilRepartidor')->with('exito', '¡Pedido tomado! Ya aparece en tus envíos en camino.');
    }

    public function marcarEntregado(int $envioId): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $repartidor = Repartidor::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $repartidor) {
            return redirect('/repartidor');
        }

        $envio = Envio::with('pedido')
            ->where('CodigoEnvio', $envioId)
            ->where('CodigoRepartidor', $repartidor->CodigoRepartidor)
            ->firstOrFail();

        $envio->pedido->update(['Estado' => 'Entregado']);

        return redirect('/perfilRepartidor')->with('exito', '¡Pedido marcado como entregado!');
    }
}
