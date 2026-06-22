<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    public function up(): void
    {
        // Restaurar trigger sin CreadoEn
        DB::unprepared('DROP TRIGGER IF EXISTS crear_pedido_automaticamente');
        DB::unprepared("
            CREATE TRIGGER crear_pedido_automaticamente
            AFTER INSERT ON envio
            FOR EACH ROW
            BEGIN
                INSERT INTO pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, Estado)
                VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), 'Iniciando');
            END
        ");

        DB::statement('ALTER TABLE pedido DROP COLUMN CreadoEn');
    }

    public function down(): void
    {
        DB::statement('ALTER TABLE pedido ADD COLUMN CreadoEn DATETIME NULL AFTER FechaPedido');

        DB::unprepared('DROP TRIGGER IF EXISTS crear_pedido_automaticamente');
        DB::unprepared("
            CREATE TRIGGER crear_pedido_automaticamente
            AFTER INSERT ON envio
            FOR EACH ROW
            BEGIN
                INSERT INTO pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, CreadoEn, Estado)
                VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), NOW(), 'Iniciando');
            END
        ");
    }
};
