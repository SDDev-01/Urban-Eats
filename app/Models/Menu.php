<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Menu extends Model
{
    protected $table = 'menu';

    protected $primaryKey = 'CodigoMenu';

    public $timestamps = false;

    protected $fillable = [
        'Categoria',
        'CodigoRestaurante',
    ];

    // cardinalidad

    public function restaurante()
    {
        return $this->belongsTo(Restaurante::class, 'CodigoRestaurante');
    }

    public function plato()
    {
        return $this->hasMany(Plato::class, 'CodigoMenu');
    }
}
