<?php

namespace App\Http\Controllers;

use App\Models\Gerente;
use App\Models\Menu;
use App\Models\Plato;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;

class PerfilRestauranteController extends Controller
{
    public function mostrarPerfil()
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        $gerente = Gerente::where('CodigoUsuario', session('CodigoUsuario'))->first();

        if (! $gerente) {
            return redirect('/restaurante')->with('info', 'Primero crea tu restaurante.');
        }

        $codigoActivo = session('restaurante_activo');
        $restaurante = $codigoActivo
            ? $gerente->restaurante()->where('CodigoRestaurante', $codigoActivo)->first()
            : $gerente->restaurante()->first();

        if (! $restaurante) {
            return redirect('/seleccion-restaurante');
        }

        $estadoMap = [
            'En Proceso' => 'nuevo',
            'Entregado' => 'entregado',
            'Cancelado' => 'rechazado',
        ];

        $pedidosHoy = $restaurante->pedido()
            ->whereDate('FechaPedido', today())
            ->with([
                'envio.cliente.usuario.telefono',
                'envio.cliente.usuario.direccion',
                'envio.pago',
            ])
            ->get();

        $pedidosJS = $pedidosHoy->map(function ($pedido) use ($estadoMap) {
            $envio = $pedido->envio;
            $usuario = $envio?->cliente?->usuario;
            $pago = $envio?->pago?->first();

            return [
                'id' => 'P-'.str_pad($pedido->CodigoPedido, 3, '0', STR_PAD_LEFT),
                'cliente' => $usuario ? trim($usuario->Nombres.' '.$usuario->Apellidos) : '—',
                'direccion' => $usuario?->direccion?->first()?->Direccion ?? '—',
                'telefono' => $usuario?->telefono?->first()?->Telefono ?? '—',
                'items' => [$envio?->Descripcion ?? 'Sin descripción'],
                'total' => (float) ($pago?->Monto ?? 0),
                'estado' => $estadoMap[$pedido->Estado] ?? 'nuevo',
                'hora' => $envio?->HoraEntrega ? substr($envio->HoraEntrega, 0, 5) : '—',
            ];
        });

        $menus = Menu::where('CodigoRestaurante', $restaurante->CodigoRestaurante)
            ->with('plato')
            ->get();

        return view('perfilRestaurante', compact('restaurante', 'pedidosJS', 'menus'));
    }

    public function crearPlato(Request $request)
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        Plato::create([
            'CodigoMenu' => $request->input('CodigoMenu'),
            'Nombre' => $request->input('Nombre'),
            'Descripcion' => $request->input('Descripcion'),
            'Precio' => $request->input('Precio'),
            'TipoComida' => $request->input('TipoComida'),
            'Disponibilidad' => $request->input('Disponibilidad', 'Disponible'),
        ]);

        return redirect('/perfilRestaurante')->with('exito', '¡Plato creado exitosamente!');
    }

    public function actualizarDisponibilidad(Request $request, int $id): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        Plato::findOrFail($id)->update([
            'Disponibilidad' => $request->input('Disponibilidad'),
        ]);

        return redirect('/perfilRestaurante')->with('exito', '¡Disponibilidad actualizada!');
    }

    public function eliminarPlato(int $id): RedirectResponse
    {
        if ($redireccion = $this->requiereLogin()) {
            return $redireccion;
        }

        Plato::findOrFail($id)->delete();

        return redirect('/perfilRestaurante')->with('exito', '¡Plato eliminado!');
    }
}
