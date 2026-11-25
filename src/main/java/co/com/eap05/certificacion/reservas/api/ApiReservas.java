package co.com.eap05.certificacion.reservas.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ApiReservas {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15)).build();
    private static final String BASE = System.getProperty("reservas.api.url",
            System.getenv().getOrDefault("RESERVAS_API_URL", "https://fabrica-2026-2-eap05-1.onrender.com"))
            .replaceAll("/+$", "");

    private final String token;

    private ApiReservas(String token) {
        this.token = token;
    }

    public static ApiReservas publica() {
        return new ApiReservas(null);
    }

    public static ApiReservas iniciarSesion(String correo, String clave) {
        Respuesta respuesta = publica().enviar("POST", "/api/auth/login",
                Map.of("email", correo, "contrasena", clave));
        if (respuesta.estado() != 200) {
            throw new AssertionError("Inicio de sesión en la API de QA: HTTP " + respuesta.estado());
        }
        return new ApiReservas(respuesta.campo("token"));
    }

    public Respuesta get(String ruta) {
        return enviar("GET", ruta, null);
    }

    public Respuesta post(String ruta, Map<String, ?> cuerpo) {
        return enviar("POST", ruta, cuerpo);
    }

    public Respuesta patch(String ruta, Map<String, ?> cuerpo) {
        return enviar("PATCH", ruta, cuerpo);
    }

    public Respuesta put(String ruta, Map<String, ?> cuerpo) {
        return enviar("PUT", ruta, cuerpo);
    }

    private Respuesta enviar(String metodo, String ruta, Map<String, ?> datos) {
        try {
            HttpRequest.Builder solicitud = HttpRequest.newBuilder(URI.create(BASE + ruta))
                    .timeout(Duration.ofSeconds(60)).header("Accept", "application/json");
            if (token != null) {
                solicitud.header("Authorization", "Bearer " + token);
            }
            if (datos != null) {
                solicitud.header("Content-Type", "application/json");
            }
            var cuerpo = datos == null ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(datos));
            var respuesta = HTTP.send(solicitud.method(metodo, cuerpo).build(), HttpResponse.BodyHandlers.ofString());
            return new Respuesta(metodo + " " + ruta.split("\\?", 2)[0], respuesta.statusCode(), respuesta.body());
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió la consulta de QA", error);
        } catch (IOException error) {
            throw new IllegalStateException("La API de QA no respondió para " + metodo + " " + ruta, error);
        }
    }

    public record Respuesta(String operacion, int estado, String texto) {
        public Respuesta exige(int esperado) {
            if (estado != esperado) {
                throw new AssertionError(operacion + ": esperado HTTP " + esperado + ", llegó " + estado
                        + ". " + texto.substring(0, Math.min(texto.length(), 450)));
            }
            return this;
        }

        public JsonNode json() {
            try {
                return JSON.readTree(texto);
            } catch (IOException error) {
                throw new AssertionError(operacion + " no devolvió JSON", error);
            }
        }

        public String campo(String nombre) {
            JsonNode dato = json().path(nombre);
            if (!dato.isTextual() || dato.asText().isBlank()) {
                throw new AssertionError(operacion + " no devolvió el campo " + nombre);
            }
            return dato.asText();
        }

        public String id() {
            return UUID.fromString(campo("id")).toString();
        }

        public Stream<JsonNode> filas() {
            JsonNode contenido = json();
            if (!contenido.isArray()) {
                throw new AssertionError(operacion + " no devolvió una lista");
            }
            return StreamSupport.stream(contenido.spliterator(), false);
        }

        public JsonNode fila(String clave, String valor) {
            return filas().filter(fila -> valor.equals(fila.path(clave).asText())).findFirst()
                    .orElseThrow(() -> new AssertionError(operacion + " no incluyó " + clave + "=" + valor));
        }
    }
}
