<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\LoginController;
use App\Http\Controllers\RegistroController;
use App\Http\Controllers\LogoutController;
use App\Http\Controllers\ChatbotController;
use App\Http\Controllers\PerfilController;

//por ahora estaticas, si despues tomamos datos entonces ahi si usamos route:get o post

//vistas estaticas
Route::view('/','index');
Route::view('/carrito','carrito');
Route::view('/catalogo','catalogo');
Route::view('/cliente','cliente');
Route::view('/mapa','mapa');
Route::view('/pago','pago');
Route::view('/perfilRestaurante','perfilRestaurante');
Route::view('/rastreo','rastreo');
Route::view('/repartidor','repartidor');
Route::view('/restaurante','restaurante');
Route::view('/restauranteDetalle','restauranteDetalle');
Route::view('/restaurantes','restaurantes');

//login
//get es cuando entran a la pagina, y post es cuando dan a enviar
Route::get('/login',[LoginController::class, 'mostrarPagina']);
Route::post('/login',[LoginController::class, 'iniciarSesion']);

//registrarse
Route::get('/registro',[RegistroController::class, 'mostrarPagina']);
Route::post('/registro',[RegistroController::class, 'Registrarse']);

//cerrar sesion
Route::post('/logout',[LogoutController::class, 'logout']);

//perfil
Route::get('/perfil',[PerfilController::class, 'MostrarDatos']);

//chatbot
Route::post('/chatbot',[ChatbotController::class, 'responder']);
