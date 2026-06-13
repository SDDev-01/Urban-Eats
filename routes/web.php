<?php

use Illuminate\Support\Facades\Route;

//por ahora estaticas, si despues tomamos datos entonces ahi si usamos route:get

Route::view('/','index');
Route::view('/carrito','carrito');
Route::view('/catalogo','catalogo');
Route::view('/cliente','cliente');
Route::view('/login','login');
Route::view('/mapa','mapa');
Route::view('/pago','pago');
Route::view('/perfil','perfil');
Route::view('/perfilRestaurante','perfilRestaurante');
Route::view('/rastreo','rastreo');
Route::view('/registro','registro');
Route::view('/repartidor','repartidor');
Route::view('/restaurante','restaurante');
Route::view('/restauranteDetalle','restauranteDetalle');
Route::view('/restaurantes','restaurantes');

/* 
Route::get('/', function () {
    return view('welcome');
});
*/