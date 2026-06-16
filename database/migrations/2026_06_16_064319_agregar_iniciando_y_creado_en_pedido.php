<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    public function up(): void
    {
        // Añadir 'Iniciando' al ENUM de Estado y columna CreadoEn
        DB::statement("ALTER TABLE pedido
            MODIFY COLUMN Estado ENUM('Iniciando','En Proceso','Entregado','Cancelado') NOT NULL,
            ADD COLUMN CreadoEn DATETIME NULL AFTER FechaPedido");

        // Actualizar el trigger para usar 'Iniciando' y poblar CreadoEn
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

    public function down(): void
    {
        DB::unprepared('DROP TRIGGER IF EXISTS crear_pedido_automaticamente');
        DB::unprepared("
            CREATE TRIGGER crear_pedido_automaticamente
            AFTER INSERT ON envio
            FOR EACH ROW
            BEGIN
                INSERT INTO pedido (CodigoEnvio, CodigoRestaurante, FechaPedido, Estado)
                VALUES (NEW.CodigoEnvio, NEW.CodigoRestaurante, CURDATE(), 'En Proceso');
            END
        ");

        DB::statement("ALTER TABLE pedido
            DROP COLUMN CreadoEn,
            MODIFY COLUMN Estado ENUM('En Proceso','Entregado','Cancelado') NOT NULL");
    }
};
