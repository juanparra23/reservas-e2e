package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.utils.DatosUnicos;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

final class RegistroDeEvento {
    private RegistroDeEvento() {
    }

    static ApiReservas.Respuesta crear(ApiReservas admin, int horas, int capacidad) {
        OffsetDateTime inicio = OffsetDateTime.now(ZoneOffset.UTC).plusHours(horas).withNano(0);
        return admin.post("/api/eventos/registrar", Map.of("nombre", DatosUnicos.nombre("Evento QA"),
                "descripcion", DatosDeReserva.DESCRIPCION, "inicio", inicio.toString(),
                "fin", inicio.plusHours(1).toString(), "capacidad", capacidad)).exige(201);
    }
}
