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
use App\Http\Controllers\RestauranteController;
use App\Http\Controllers\RestauranteDetalleController;
use Illuminate\Support\Facades\Route;

// por ahora estaticas, si despues tomamos datos entonces ahi si usamos route:get o post

// vistas estaticas
Route::view('/', 'index');
Route::view('/carrito', 'carrito');
Route::get('/catalogo', [CatalogoController::class, 'mostrar']);
Route::view('/cliente', 'cliente');
Route::view('/mapa', 'mapa');
Route::view('/pago', 'pago');
Route::get('/rastreo', [RastreoController::class, 'mostrar']);
Route::post('/pedido/confirmar', [PedidoController::class, 'confirmar']);
Route::view('/repartidor', 'repartidor');

// login
// get es cuando entran a la pagina, y post es cuando dan a enviar
Route::get('/login', [LoginController::class, 'mostrarPagina']);
Route::post('/login', [LoginController::class, 'iniciarSesion']);

// registrarse
Route::get('/registro', [RegistroController::class, 'mostrarPagina']);
Route::post('/registro', [RegistroController::class, 'Registrarse']);

// cerrar sesion
Route::post('/logout', [LogoutController::class, 'logout']);

// perfil
Route::get('/perfil', [PerfilController::class, 'MostrarDatos']);

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
