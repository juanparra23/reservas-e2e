package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class AgendaStepDefinitions {
    private final DatosDeReserva datos = new DatosDeReserva();
    private ApiReservas.Respuesta respuesta;

    @Given("que un proveedor activo inició sesión")
    public void sesionDelProveedor() {
        datos.proveedor();
    }

    @Given("que el proveedor está asignado a un servicio")
    public void crearServicioAsignado() {
        datos.prepararServicioConProveedor();
    }

    @Given("no tiene horarios que se crucen con el período seleccionado")
    public void prepararPeriodo() {
        datos.asignarPeriodo(48);
    }

    @When("publica un horario con inicio anterior al fin")
    public void publicar() {
        respuesta = datos.publicarHorario(datos.inicio, datos.fin);
    }

    @Then("el horario debe quedar registrado como disponible")
    public void disponible() {
        respuesta.exige(201).id();
        assertEquals("disponible", respuesta.campo("estado"));
    }

    @Then("el horario debe conservar exactamente sus fechas de inicio y fin")
    public void verificarFechas() {
        assertEquals(datos.inicio, LocalDateTime.parse(respuesta.campo("inicio")));
        assertEquals(datos.fin, LocalDateTime.parse(respuesta.campo("fin")));
    }

    @Given("que el proveedor tiene un horario previamente registrado")
    public void publicarHorarioDePrueba() {
        datos.prepararHorario(48, false, false);
    }

    @When("intenta publicar otro horario que se cruza con el existente")
    public void intentarSolapamiento() {
        respuesta = datos.publicarHorario(datos.inicio.plusMinutes(10), datos.fin.plusMinutes(10));
    }

    @Then("el nuevo horario no debe registrarse")
    public void rechazarSolapamiento() {
        respuesta.exige(400);
        assertEquals(1, datos.disponibilidad().filas().count());
    }

    @Then("debe mostrarse que existe un solapamiento")
    public void verificarMensajeSolapamiento() {
        assertTrue(respuesta.texto().contains("solapa"));
    }

    @Given("que el proveedor tiene una reserva confirmada asociada a uno de sus horarios")
    public void prepararAgendaConReserva() {
        publicarHorarioDePrueba();
        datos.confirmarReserva(datos.usuarios.cliente());
    }

    @Given("que el proveedor tiene un horario asociado a una reserva confirmada")
    public void prepararHorarioReservado() {
        prepararAgendaConReserva();
    }

    @Given("que el proveedor no tiene reservas asociadas")
    public void prepararAgendaVacia() {
        datos.proveedor();
    }

    @When("consulta su agenda")
    public void consultarAgenda() {
        respuesta = datos.proveedor().get("/api/agenda").exige(200);
    }

    @Then("debe visualizar la reserva confirmada")
    public void verificarReservaEnAgenda() {
        assertEquals("confirmada", reservaEnAgenda(respuesta).path("estado").asText());
    }

    @Then("debe visualizar la fecha y hora correspondientes")
    public void verificarFechaEnAgenda() {
        JsonNode reserva = reservaEnAgenda(respuesta);
        assertEquals(OffsetDateTime.parse(datos.confirmacion.campo("horarioInicio")).toInstant(),
                OffsetDateTime.parse(reserva.path("fechaInicio").asText()).toInstant());
        assertEquals(OffsetDateTime.parse(datos.confirmacion.campo("horarioFin")).toInstant(),
                OffsetDateTime.parse(reserva.path("fechaFin").asText()).toInstant());
    }

    @Then("debe informarse que no existen reservas programadas")
    public void verificarAgendaVacia() {
        assertTrue(respuesta.campo("mensaje").contains("No existen reservas programadas"));
    }

    @Given("que el proveedor tiene un horario disponible sin reservas confirmadas")
    public void prepararHorarioSinReserva() {
        publicarHorarioDePrueba();
    }

    @When("bloquea el horario")
    public void bloquearHorario() {
        respuesta = datos.proveedor().patch("/api/horarios/" + datos.horarioId + "/bloquear", null);
    }

    @When("intenta bloquear el horario")
    public void intentarBloqueo() {
        bloquearHorario();
    }

    @Then("el horario debe quedar en estado bloqueado")
    public void verificarBloqueo() {
        assertEquals("bloqueado", respuesta.exige(200).campo("estado"));
        assertEquals(datos.horarioId, respuesta.id());
    }

    @Then("no debe aparecer como disponible para los clientes")
    public void verificarNoSeOfrece() {
        assertFalse(datos.disponibilidad().filas().anyMatch(fila -> datos.horarioId.equals(fila.path("id").asText())));
    }

    @Then("la operación debe rechazarse porque existe una reserva")
    public void verificarBloqueoRechazado() {
        assertTrue(respuesta.exige(409).campo("error").contains("reserva confirmada"));
    }

    @Then("el horario y la reserva deben permanecer sin cambios")
    public void verificarReservaIntacta() {
        JsonNode reserva = reservaEnAgenda(datos.proveedor().get("/api/agenda").exige(200));
        assertEquals(datos.horarioId, reserva.path("horarioId").asText());
        assertEquals("confirmada", reserva.path("estado").asText());
        verificarNoSeOfrece();
    }

    @Given("que el proveedor tiene un horario que finaliza a las diez de la mañana")
    public void prepararPrimerHorarioContiguo() {
        crearServicioAsignado();
        datos.fin = LocalDateTime.now(ZoneOffset.UTC).plusDays(3).toLocalDate().atTime(10, 0);
        datos.inicio = datos.fin.minusMinutes(30);
        datos.horarioId = datos.publicarHorario(datos.inicio, datos.fin).exige(201).id();
    }

    @When("publica otro horario que inicia exactamente a las diez de la mañana")
    public void publicarSegundoHorarioContiguo() {
        respuesta = datos.publicarHorario(datos.fin, datos.fin.plusMinutes(30));
    }

    @Then("el nuevo horario debe quedar registrado como disponible")
    public void verificarSegundoHorario() {
        disponible();
    }

    @Then("ambos horarios deben conservar sus períodos independientes")
    public void verificarPeriodosContiguos() {
        assertNotEquals(datos.horarioId, respuesta.id());
        assertEquals(datos.fin, LocalDateTime.parse(respuesta.campo("inicio")));
        assertEquals(datos.fin.plusMinutes(30), LocalDateTime.parse(respuesta.campo("fin")));
        assertEquals(2, datos.disponibilidad().filas().count());
    }

    private JsonNode reservaEnAgenda(ApiReservas.Respuesta agenda) {
        return agenda.fila("id", datos.reservaId);
    }
}
