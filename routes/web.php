<?php

use App\Http\Controllers\CatalogoController;
use App\Http\Controllers\LoginController;
use App\Http\Controllers\LogoutController;
use App\Http\Controllers\MenuController;
use App\Http\Controllers\PedidoController;
use App\Http\Controllers\PerfilController;
use App\Http\Controllers\PerfilRestauranteController;
use App\Http\Controllers\RastreoController;
use App\Http\Controllers\RegistroController;
use App\Http\Controllers\RepartidorController;
use App\Http\Controllers\RestauranteController;
use App\Http\Controllers\RestauranteDetalleController;
use App\Http\Controllers\SeleccionRolController;
use App\Http\Controllers\PagoController;
use Illuminate\Support\Facades\Route;

// por ahora estaticas, si despues tomamos datos entonces ahi si usamos route:get o post
// get es cuando entran a la pagina, y post es cuando dan a enviar

// vistas estaticas
Route::view('/', 'index');
Route::view('/carrito', 'carrito');
Route::view('/cliente', 'cliente');
Route::view('/mapa', 'mapa');

//catalogo
Route::get('/catalogo', [CatalogoController::class, 'mostrar']);

// login
Route::get('/login', [LoginController::class, 'mostrarPagina']);
Route::post('/login', [LoginController::class, 'iniciarSesion']);

// registrarse
Route::get('/registro', [RegistroController::class, 'mostrarPagina']);
Route::post('/registro', [RegistroController::class, 'Registrarse']);

// cerrar sesion
Route::post('/logout', [LogoutController::class, 'logout']);

// seleccion de rol (gerentes)
Route::get('/seleccion-rol', [SeleccionRolController::class, 'mostrarRol']);
Route::get('/seleccion-restaurante', [SeleccionRolController::class, 'mostrarRestaurantes']);
Route::post('/seleccion-restaurante/{id}', [SeleccionRolController::class, 'confirmarRestaurante']);

// perfil
Route::get('/perfil', [PerfilController::class, 'MostrarDatos']);
Route::patch('/perfil', [PerfilController::class, 'actualizar']);

// restaurante
Route::get('/restaurante', [RestauranteController::class, 'mostrarPagina']);
Route::post('/restaurante', [RestauranteController::class, 'crearRestaurante']);
Route::get('/restauranteDetalle', [RestauranteDetalleController::class, 'mostrarDetalle']);
Route::get('/restaurantes', [RestauranteController::class, 'listar']);
Route::get('/perfilRestaurante', [PerfilRestauranteController::class, 'mostrarPerfil']);
Route::post('/perfilRestaurante/plato', [PerfilRestauranteController::class, 'crearPlato']);
Route::patch('/perfilRestaurante/plato/{id}', [PerfilRestauranteController::class, 'actualizarDisponibilidad']);
Route::delete('/perfilRestaurante/plato/{id}', [PerfilRestauranteController::class, 'eliminarPlato']);
Route::get('/menu', [MenuController::class, 'mostrarFormulario']);
Route::post('/menu', [MenuController::class, 'crearMenu']);

//repartidor
Route::get('/repartidor', [RepartidorController::class, 'mostrarFormulario']);
Route::post('/repartidor', [RepartidorController::class, 'registrar']);
Route::get('/perfilRepartidor', [RepartidorController::class, 'mostrarPerfil']);
Route::post('/repartidor/tomar/{envioId}', [RepartidorController::class, 'tomarPedido']);
Route::patch('/repartidor/entregar/{envioId}', [RepartidorController::class, 'marcarEntregado']);

//pedido
Route::get('/rastreo', [RastreoController::class, 'mostrar']);
Route::post('/pedido/confirmar', [PedidoController::class, 'confirmar']);
Route::get('/pedido/{id}/estado', [PedidoController::class, 'estado']);
Route::post('/pedido/{id}/cancelar', [PedidoController::class, 'cancelar']);

//pago
Route::get('/pago', [PagoController::class, 'mostrarPagina']);
