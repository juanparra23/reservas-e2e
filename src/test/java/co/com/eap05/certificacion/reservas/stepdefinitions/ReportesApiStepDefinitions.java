package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public class ReportesApiStepDefinitions {
    private final DatosDeReserva datos = new DatosDeReserva();
    private ApiReservas.Respuesta resultado;
    private LocalDateTime desde;
    private LocalDateTime hasta;
    private String eventoId;

    @Given("que existen servicios con reservas registradas dentro de un período")
    public void prepararUso() {
        crearReserva();
        LocalDateTime creada = creadaEn();
        desde = creada.minusMinutes(1);
        hasta = creada.plusMinutes(1);
    }

    @Given("que existen servicios con horarios y reservas dentro de un período")
    public void prepararOcupacion() {
        crearReserva();
        desde = datos.inicio.minusMinutes(1);
        hasta = datos.fin.plusMinutes(1);
    }

    @Given("que existen eventos con reservas confirmadas dentro de un período")
    public void prepararEventoReservado() {
        ApiReservas.Respuesta evento = RegistroDeEvento.crear(datos.usuarios.admin(), 48, 2);
        eventoId = evento.id();
        datos.usuarios.cliente().post("/api/eventos/" + eventoId + "/reservas", null).exige(201);
        LocalDateTime inicio = OffsetDateTime.parse(evento.campo("inicio")).toLocalDateTime();
        desde = inicio.minusMinutes(1);
        hasta = inicio.plusMinutes(1);
    }

    @Given("que existen reservas confirmadas exactamente al inicio y al final de un período")
    public void prepararReservasEnLosLimites() {
        crearReserva();
        desde = creadaEn();
        datos.horarioId = datos.publicarHorario(datos.fin, datos.fin.plusMinutes(30)).exige(201).id();
        datos.confirmarReserva(datos.usuarios.cliente());
        hasta = creadaEn();
        assertTrue(desde.isBefore(hasta));
    }

    @When("consulta por API el uso de los servicios en el período")
    public void consultarUso() {
        consultar("/api/servicios/uso");
    }

    @When("consulta por API la ocupación de los servicios en el período")
    public void consultarOcupacion() {
        consultar("/api/reservas/ocupacion-servicios");
    }

    @When("solicita el reporte de ocupación de eventos para ese período")
    public void consultarOcupacionEvento() {
        consultar("/api/eventos/ocupacion");
    }

    @Then("debe visualizar cada servicio y su cantidad correcta de reservas")
    @Then("el reporte cuenta la reserva del inicio y excluye la del final")
    public void verificarConteo() {
        JsonNode fila = resultado.fila("servicioId", datos.servicioId);
        assertEquals(datos.servicioNombre, fila.path("nombre").asText());
        assertEquals(1, fila.path("reservas").asInt(-1));
    }

    @Then("debe visualizar horarios totales, horarios reservados y porcentaje por servicio")
    public void verificarOcupacionServicio() {
        JsonNode fila = resultado.fila("servicioId", datos.servicioId);
        assertEquals(1, fila.path("totalHorarios").asInt(-1));
        assertEquals(1, fila.path("horariosReservados").asInt(-1));
        assertEquals(100.0, fila.path("porcentajeOcupacion").asDouble(-1), 0.01);
    }

    @Then("ningún servicio debe tener más horarios reservados que horarios totales")
    public void verificarCoherencia() {
        resultado.filas().forEach(fila -> assertTrue(
                "El servicio " + fila.path("servicioId").asText() + " tiene más reservas que horarios ofertados",
                fila.path("horariosReservados").asLong() <= fila.path("totalHorarios").asLong()));
    }

    @Then("debe visualizar los cupos reservados y el porcentaje de ocupación de cada evento")
    public void verificarOcupacionEvento() {
        JsonNode fila = resultado.fila("eventoId", eventoId);
        assertEquals(2, fila.path("capacidad").asInt(-1));
        assertEquals(1, fila.path("reservados").asInt(-1));
        assertEquals(50.0, fila.path("porcentajeOcupacion").asDouble(-1), 0.01);
    }

    private void crearReserva() {
        datos.prepararHorario(48, false, false);
        datos.confirmarReserva(datos.usuarios.cliente());
    }

    private LocalDateTime creadaEn() {
        return OffsetDateTime.parse(datos.confirmacion.campo("creadaEn")).toLocalDateTime();
    }

    private void consultar(String ruta) {
        resultado = datos.usuarios.admin().get(ruta + "?desde=" + desde + "&hasta=" + hasta).exige(200);
    }
}
