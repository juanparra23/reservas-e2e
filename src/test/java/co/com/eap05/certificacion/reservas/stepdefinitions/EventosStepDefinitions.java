package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.tasks.IniciarSesion;
import co.com.eap05.certificacion.reservas.tasks.ReservarEnPantalla;
import co.com.eap05.certificacion.reservas.utils.Configuracion;
import co.com.eap05.certificacion.reservas.utils.DatosUnicos;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.CERRAR_SESION;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.junit.Assert.*;

public class EventosStepDefinitions {
    private static final String EVENTOS = "/api/eventos";
    private static final String DESCRIPCION = "Evento temporal de pruebas E2E";

    private final UsuariosDePrueba usuarios = new UsuariosDePrueba();
    private ApiReservas cliente;
    private OffsetDateTime inicio;
    private String nombre;
    private int capacidad;
    private String eventoId;
    private String reservaId;
    private ApiReservas.Respuesta evento;
    private ApiReservas.Respuesta respuesta;

    @Given("que el administrador de QA está autenticado en la API")
    public void autenticarAdministrador() {
        usuarios.admin();
    }

    @Given("que un cliente inició sesión")
    public void autenticarCliente() {
        cliente = usuarios.cliente();
    }

    @Given("dispone de un nombre único, descripción, inicio, fin y capacidad válidos")
    public void prepararEvento() {
        prepararEvento(48, 2);
    }

    @When("registra el evento")
    public void registrarEvento() {
        evento = usuarios.admin().post(EVENTOS + "/registrar", Map.of(
                "nombre", nombre, "descripcion", DESCRIPCION, "inicio", inicio.toString(),
                "fin", inicio.plusHours(1).toString(), "capacidad", capacidad)).exige(201);
        eventoId = evento.id();
    }

    @Then("el evento debe quedar registrado y activo")
    public void verificarEventoActivo() {
        assertNotNull(eventoId);
        assertTrue(evento.json().path("activo").asBoolean());
    }

    @Then("debe conservar los datos proporcionados")
    public void verificarDatosDelEvento() {
        assertEquals(nombre, evento.campo("nombre"));
        assertEquals(DESCRIPCION, evento.campo("descripcion"));
        assertEquals(capacidad, evento.json().path("capacidad").asInt(-1));
        assertEquals(inicio.toInstant(), OffsetDateTime.parse(evento.campo("inicio")).toInstant());
        assertEquals(inicio.plusHours(1).toInstant(), OffsetDateTime.parse(evento.campo("fin")).toInstant());
    }

    @Given("existe un evento activo con cupos disponibles")
    @Given("que el administrador inició sesión y existe un evento registrado")
    @Given("que el administrador registra un evento único que inicia en cuarenta y ocho horas con capacidad de dos cupos")
    public void crearEventoDisponible() {
        prepararEvento();
        registrarEvento();
    }

    @Given("existe un evento que alcanzó su capacidad máxima")
    public void llenarUnEvento() {
        prepararEvento(48, 1);
        registrarEvento();
        reservar(usuarios.nuevoCliente()).exige(201);
    }

    @Given("existe un evento con dos reservas confirmadas")
    public void crearEventoConDosReservas() {
        crearEventoDisponible();
        reservar(usuarios.cliente()).exige(201);
        reservar(usuarios.nuevoCliente()).exige(201);
    }

    @When("reserva un cupo")
    @When("intenta reservar un cupo")
    public void solicitarCupo() {
        respuesta = reservar(cliente);
    }

    @Then("la reserva debe quedar confirmada")
    public void verificarReservaConfirmada() {
        respuesta.exige(201).id();
        assertEquals("confirmada", respuesta.campo("estado"));
        assertEquals(eventoId, respuesta.campo("eventoId"));
    }

    @Then("los cupos disponibles deben disminuir en uno")
    public void verificarCupoTomado() {
        assertEquals(capacidad - 1, respuesta.json().path("cuposRestantes").asInt(-1));
        verificarOcupacion(1, capacidad);
    }

    @Then("la reserva debe rechazarse por falta de cupos")
    public void verificarSinCupos() {
        assertTrue(respuesta.exige(409).campo("mensaje").contains("No hay cupos disponibles"));
    }

    @Then("la ocupación del evento debe permanecer sin cambios")
    public void verificarEventoLleno() {
        verificarOcupacion(1, 1);
    }

    @Given("que un cliente tiene una reserva confirmada para un evento que inicia en veinticuatro horas")
    public void crearReservaAnticipada() {
        crearReservaConAnticipacion(24);
    }

    @Given("que un cliente tiene una reserva confirmada para un evento que inicia en seis horas")
    public void crearReservaProxima() {
        crearReservaConAnticipacion(6);
    }

    public void reservarComoCliente() {
        autenticarCliente();
        reservaId = reservar(cliente).exige(201).id();
    }

    @When("cancela la reserva del evento")
    @When("intenta cancelar la reserva del evento")
    public void solicitarCancelacion() {
        respuesta = cliente.post(EVENTOS + "/" + eventoId + "/reservas/" + reservaId + "/cancelar", null);
    }

    @Then("la reserva debe quedar cancelada")
    @Then("la reserva del evento debe quedar cancelada")
    public void verificarCancelacion() {
        respuesta.exige(200);
        assertEquals(reservaId, respuesta.id());
        assertEquals("cancelada", respuesta.campo("estado"));
    }

    @Given("un cliente activo reserva un cupo del evento desde el navegador")
    public void reservarEventoEnPantalla() {
        theActorCalled("Cliente").attemptsTo(
                IniciarSesion.con(Configuracion.usuarioCliente(), Configuracion.claveCliente()),
                WaitUntil.the(CERRAR_SESION, isVisible()).forNoMoreThan(45).seconds(),
                ReservarEnPantalla.evento(eventoId));
        JsonNode registro = usuarios.admin().get("/api/reservas/historial-negocio")
                .exige(200).fila("itemId", eventoId);
        assertEquals("confirmada", registro.path("estado").asText());
        reservaId = registro.path("reservaId").asText();
    }

    @When("el cliente cancela la reserva del evento desde el navegador")
    public void cancelarEventoEnPantalla() {
        theActorInTheSpotlight().attemptsTo(ReservarEnPantalla.cancelarEvento(eventoId, reservaId));
    }

    @Then("la reserva debe constar como cancelada en el historial del negocio")
    public void verificarCancelacionEnPantalla() {
        verificarHistorialCancelado();
    }

    @Then("los cupos disponibles deben aumentar en uno")
    @Then("el evento debe volver a tener dos cupos disponibles")
    @Then("el reporte del evento debe mostrar cero reservas activas y cero por ciento de ocupación")
    public void verificarCupoLiberado() {
        ApiReservas.Respuesta reporte = reporte();
        if (reporte.json().isArray()) {
            verificarOcupacion(reporte, 0, capacidad);
        } else {
            assertTrue(reporte.campo("mensaje").contains("No existen datos de ocupación"));
        }
    }

    @Then("la reserva cancelada debe aparecer en el historial del negocio")
    public void verificarHistorialCancelado() {
        assertEquals("cancelada", estadoEnHistorial());
    }

    @Then("debe mostrarse que no cumple la anticipación mínima de doce horas")
    public void verificarPlazo() {
        assertTrue(respuesta.exige(409).campo("mensaje").contains("menos de 12 horas"));
    }

    @Then("la reserva debe permanecer confirmada")
    public void verificarHistorialConfirmado() {
        assertEquals("confirmada", estadoEnHistorial());
    }

    @When("intenta establecer la capacidad del evento en cero")
    public void ponerCapacidadCero() {
        modificarCapacidad(0);
    }

    @When("intenta establecer la capacidad en uno")
    public void ponerCapacidadUno() {
        modificarCapacidad(1);
    }

    @Then("la actualización debe rechazarse")
    public void verificarActualizacionRechazada() {
        // El dato inválido y el conflicto se distinguen en sus aserciones específicas.
        assertTrue(respuesta.estado() >= 400 && respuesta.estado() < 500);
    }

    @Then("debe mostrarse que la capacidad debe ser mayor que cero")
    public void verificarMinimoDeCapacidad() {
        assertFalse(respuesta.exige(400).json().path("errores").path("capacidad").asText().isBlank());
    }

    @Then("la capacidad y las reservas deben permanecer sin cambios")
    public void verificarCapacidadYReservas() {
        respuesta.exige(409);
        verificarOcupacion(2, 2);
    }

    private void modificarCapacidad(int nuevaCapacidad) {
        respuesta = usuarios.admin().patch(EVENTOS + "/" + eventoId + "/capacidad",
                Map.of("capacidad", nuevaCapacidad));
    }

    private void prepararEvento(int horas, int cupos) {
        inicio = OffsetDateTime.now(ZoneOffset.UTC).plusHours(horas).truncatedTo(ChronoUnit.SECONDS);
        nombre = DatosUnicos.nombre("Evento QA");
        capacidad = cupos;
    }

    private void crearReservaConAnticipacion(int horas) {
        prepararEvento(horas, 2);
        registrarEvento();
        reservarComoCliente();
    }

    private ApiReservas.Respuesta reservar(ApiReservas usuario) {
        return usuario.post(EVENTOS + "/" + eventoId + "/reservas", null);
    }

    private String estadoEnHistorial() {
        JsonNode fila = usuarios.admin().get("/api/reservas/historial-negocio").exige(200).fila("reservaId", reservaId);
        assertEquals("EVENTO", fila.path("tipo").asText());
        assertEquals(eventoId, fila.path("itemId").asText());
        return fila.path("estado").asText();
    }

    private ApiReservas.Respuesta reporte() {
        LocalDateTime fecha = OffsetDateTime.parse(evento.campo("inicio")).toLocalDateTime();
        return usuarios.admin().get(EVENTOS + "/ocupacion?desde=" + fecha.minusMinutes(1)
                + "&hasta=" + fecha.plusMinutes(1)).exige(200);
    }

    private void verificarOcupacion(int reservados, int cupos) {
        verificarOcupacion(reporte(), reservados, cupos);
    }

    private void verificarOcupacion(ApiReservas.Respuesta reporte, int reservados, int cupos) {
        JsonNode fila = reporte.fila("eventoId", eventoId);
        assertEquals(reservados, fila.path("reservados").asInt(-1));
        assertEquals(cupos, fila.path("capacidad").asInt(-1));
        assertEquals(100.0 * reservados / cupos, fila.path("porcentajeOcupacion").asDouble(-1), 0.1);
    }
}
