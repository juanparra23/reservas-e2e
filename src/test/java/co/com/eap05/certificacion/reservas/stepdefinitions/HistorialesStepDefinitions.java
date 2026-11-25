package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.Assert.*;

public class HistorialesStepDefinitions {
    private final DatosDeReserva datos = new DatosDeReserva();
    private ApiReservas cliente;
    private ApiReservas.Respuesta historial;
    private String cancelada;
    private String reservaEvento;
    private String eventoId;

    @Given("que un cliente inició sesión y tiene reservas con diferentes estados")
    public void prepararHistorialCliente() {
        cliente = datos.usuarios.nuevoCliente();
        datos.prepararHorario(48, false, false);
        datos.confirmarReserva(cliente);
        datos.cancelar(cliente).exige(200);
        cancelada = datos.reservaId;
        datos.confirmarReserva(cliente);
    }

    @When("consulta su historial de cliente")
    public void consultarHistorialCliente() {
        historial = cliente.get("/api/reservas/historial").exige(200);
    }

    @Then("debe visualizar únicamente sus reservas de servicios")
    public void verificarHistorialCliente() {
        assertEquals(2, historial.filas().count());
        assertEquals(datos.servicioId, historial.fila("reservaId", datos.reservaId).path("servicioId").asText());
        assertEquals("cancelada", historial.fila("reservaId", cancelada).path("estado").asText());
    }

    @Given("que un proveedor inició sesión y tiene reservas asociadas a sus horarios")
    public void prepararHistorialProveedor() {
        datos.prepararHorario(48, true, true);
        datos.confirmarReserva(datos.usuarios.cliente());
    }

    @When("consulta su historial de proveedor")
    public void consultarHistorialProveedor() {
        historial = datos.proveedor().get("/api/reservas/historial-proveedor").exige(200);
    }

    @Then("debe visualizar únicamente las reservas relacionadas con sus servicios")
    public void verificarHistorialProveedor() {
        assertEquals(1, historial.filas().count());
        JsonNode fila = historial.fila("reservaId", datos.reservaId);
        assertEquals(datos.servicioId, fila.path("servicioId").asText());
        assertEquals(datos.horarioId, fila.path("horarioId").asText());
    }

    @Then("cada reserva debe mostrar su estado actual")
    public void verificarEstado() {
        assertEquals("confirmada", historial.fila("reservaId", datos.reservaId).path("estado").asText());
    }

    @Given("que el administrador tiene reservas de servicios y eventos para consultar")
    public void prepararHistorialNegocio() {
        prepararHistorialProveedor();
        eventoId = RegistroDeEvento.crear(datos.usuarios.admin(), 48, 2).id();
        reservaEvento = datos.usuarios.cliente().post("/api/eventos/" + eventoId + "/reservas", null).exige(201).id();
    }

    @When("consulta el historial del negocio")
    public void consultarHistorialNegocio() {
        historial = datos.usuarios.admin().get("/api/reservas/historial-negocio").exige(200);
    }

    @Then("debe visualizar las reservas de servicios y eventos")
    public void verificarReservasDeAmbosTipos() {
        assertEquals(datos.servicioId, historial.fila("reservaId", datos.reservaId).path("itemId").asText());
        assertEquals(eventoId, historial.fila("reservaId", reservaEvento).path("itemId").asText());
    }

    @Then("cada registro debe mostrar su tipo y estado actual")
    public void verificarTiposYEstados() {
        Map.of(datos.reservaId, "SERVICIO", reservaEvento, "EVENTO").forEach((id, tipo) -> {
            JsonNode fila = historial.fila("reservaId", id);
            assertEquals(tipo, fila.path("tipo").asText());
            assertEquals("confirmada", fila.path("estado").asText());
        });
    }
}
