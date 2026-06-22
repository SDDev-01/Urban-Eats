<?php

namespace App\Http\Controllers;

use App\Models\Restaurante;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class ChatbotController extends Controller
{
    private const HORARIO_KEYWORDS = ['horario', 'hora', 'abre', 'cierra', 'disponible', 'cuando', 'abierto'];
    private const UBICACION_KEYWORDS = ['donde', 'ubicación', 'ubicacion', 'direccion', 'dirección', 'lugar', 'mapa'];
    private const MENU_KEYWORDS = ['menu', 'menú', 'plato', 'comida', 'comer', 'pedir', 'precio', 'costo', 'valor'];

    public function responder(Request $request): JsonResponse
    {
        $request->validate(['mensaje' => 'required|string|max:1000']);

        $mensaje = trim($request->input('mensaje'));
        $mensajeLower = mb_strtolower($mensaje);

        if ($this->contienePalabras($mensajeLower, self::HORARIO_KEYWORDS)) {
            return $this->respuestaHorarios();
        }

        if ($this->contienePalabras($mensajeLower, self::UBICACION_KEYWORDS)) {
            return $this->respuestaUbicaciones();
        }

        return $this->respuestaIA($mensaje);
    }

    private function contienePalabras(string $texto, array $palabras): bool
    {
        foreach ($palabras as $palabra) {
            if (str_contains($texto, $palabra)) {
                return true;
            }
        }

        return false;
    }

    private function respuestaHorarios(): JsonResponse
    {
        $restaurantes = Restaurante::select('Nombre', 'Horario')->get();

        if ($restaurantes->isEmpty()) {
            return response()->json([
                'respuesta' => 'Actualmente no tenemos información de horarios disponible. ¡Contáctanos para más detalles!',
            ]);
        }

        $lineas = $restaurantes->map(fn ($r) => "• **{$r->Nombre}**: {$r->Horario}")->implode("\n");

        return response()->json([
            'respuesta' => "¡Claro! Aquí están los horarios de nuestros restaurantes:\n\n{$lineas}\n\n¿Hay algo más en lo que pueda ayudarte?",
        ]);
    }

    private function respuestaUbicaciones(): JsonResponse
    {
        $restaurantes = Restaurante::select('Nombre', 'Ubicacion')->get();

        if ($restaurantes->isEmpty()) {
            return response()->json([
                'respuesta' => 'No tenemos información de ubicaciones disponible en este momento.',
            ]);
        }

        $lineas = $restaurantes->map(fn ($r) => "• **{$r->Nombre}**: {$r->Ubicacion}")->implode("\n");

        return response()->json([
            'respuesta' => "¡Con gusto! Estas son nuestras ubicaciones:\n\n{$lineas}\n\n¿Necesitas ayuda con algo más?",
        ]);
    }

    private function respuestaIA(string $mensaje): JsonResponse
    {
        $apiKey = env('ANTHROPIC_API_KEY');

        if (empty($apiKey)) {
            return response()->json([
                'respuesta' => 'Lo siento, el asistente de IA no está disponible en este momento. ¿Puedo ayudarte con horarios o ubicaciones?',
            ]);
        }

        $contexto = $this->construirContexto();

        $systemPrompt = <<<PROMPT
        Eres UrbanBot, el asistente virtual de Urban Eats, una plataforma de comida saludable en Bogotá.
        Responde siempre en español, de forma amigable, concisa y útil. No inventes información.
        Si no puedes responder con los datos disponibles, sugiere al usuario que visite la sección de restaurantes.

        INFORMACIÓN ACTUAL DE URBAN EATS:
        {$contexto}
        PROMPT;

        $cuerpo = json_encode([
            'model' => 'claude-haiku-4-5',
            'max_tokens' => 512,
            'system' => $systemPrompt,
            'messages' => [
                ['role' => 'user', 'content' => $mensaje],
            ],
        ]);

        $ch = curl_init('https://api.anthropic.com/v1/messages');
        curl_setopt_array($ch, [
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => true,
            CURLOPT_POSTFIELDS => $cuerpo,
            CURLOPT_HTTPHEADER => [
                'x-api-key: '.$apiKey,
                'anthropic-version: 2023-06-01',
                'content-type: application/json',
            ],
            CURLOPT_TIMEOUT => 20,
        ]);

        $respuestaRaw = curl_exec($ch);
        $error = curl_error($ch);
        curl_close($ch);

        if ($error || ! $respuestaRaw) {
            return response()->json([
                'respuesta' => 'Tuve un problema al conectarme. Por favor, intenta de nuevo en un momento.',
            ]);
        }

        $datos = json_decode($respuestaRaw, true);
        $texto = $datos['content'][0]['text'] ?? 'No pude generar una respuesta. ¿Puedo ayudarte con horarios o el menú?';

        return response()->json(['respuesta' => $texto]);
    }

    private function construirContexto(): string
    {
        $restaurantes = Restaurante::with(['menu.plato'])->get();

        if ($restaurantes->isEmpty()) {
            return 'No hay restaurantes registrados actualmente.';
        }

        $lineas = [];

        foreach ($restaurantes as $restaurante) {
            $lineas[] = "RESTAURANTE: {$restaurante->Nombre}";
            $lineas[] = "  Ubicación: {$restaurante->Ubicacion}";
            $lineas[] = "  Horario: {$restaurante->Horario}";

            foreach ($restaurante->menu as $menu) {
                $lineas[] = "  Categoría de menú: {$menu->Categoria}";

                foreach ($menu->plato as $plato) {
                    $disponible = $plato->Disponibilidad ? 'Disponible' : 'No disponible';
                    $lineas[] = "    - {$plato->Nombre} | \${$plato->Precio} | {$plato->TipoComida} | {$disponible}";

                    if (! empty($plato->Descripcion)) {
                        $lineas[] = "      Descripción: {$plato->Descripcion}";
                    }
                }
            }

            $lineas[] = '';
        }

        return implode("\n", $lineas);
    }
}
