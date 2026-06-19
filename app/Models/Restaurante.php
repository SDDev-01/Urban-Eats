<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Restaurante extends Model
{
    protected $table = 'restaurante';

    protected $primaryKey = 'CodigoRestaurante';

    public $timestamps = false;

    protected $fillable = [
        'CodigoCiudad',
        'CodigoGerente',
        'Nombre',
        'Direccion',
        'Horario',
    ];

    // cardinalidad

    public function gerente()
    {
        return $this->belongsTo(Gerente::class, 'CodigoGerente');
    }

    public function ciudad()
    {
        return $this->belongsTo(Ciudades::class, 'CodigoCiudad');
    }

    public function pedido()
    {
        return $this->hasMany(Pedido::class, 'CodigoRestaurante');
    }

    public function menu()
    {
        return $this->hasMany(Menu::class, 'CodigoRestaurante');
    }
}
