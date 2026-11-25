package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.model.Cliente;
import co.com.eap05.certificacion.reservas.model.ContextoEscenario;
import co.com.eap05.certificacion.reservas.tasks.AbrirRuta;
import co.com.eap05.certificacion.reservas.tasks.IniciarSesion;
import co.com.eap05.certificacion.reservas.tasks.RegistrarCliente;
import co.com.eap05.certificacion.reservas.tasks.ReservarEnPantalla;
import co.com.eap05.certificacion.reservas.utils.Configuracion;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.CERRAR_SESION;
import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.TITULO_LOGIN;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.junit.Assert.*;

public class ReservasStepDefinitions {
    private final DatosDeReserva datos = new DatosDeReserva();
    private ApiReservas.Respuesta resultado;
    private Cliente clienteNuevo;
    private JsonNode reservaDelFlujo;

    @Given("que un cliente activo inició sesión")
    public void iniciarSesionComoCliente() {
        datos.usuarios.cliente();
    }

    @Given("que existe un servicio activo con un horario futuro disponible")
    public void prepararHorarioSinRecurso() {
        datos.prepararHorario(48, false, false);
    }

    @Given("que existe un servicio activo sin horarios disponibles")
    public void prepararServicioSinHorarios() {
        datos.prepararServicioConProveedor();
    }

    @When("el cliente consulta la disponibilidad del servicio")
    public void consultarDisponibilidad() {
        resultado = datos.disponibilidad();
    }

    @Then("debe visualizar el horario disponible")
    public void verificarHorarioDisponible() {
        resultado.fila("id", datos.horarioId);
    }

    @Then("debe informarse que no existen horarios disponibles")
    public void verificarDisponibilidadVacia() {
        assertEquals(0, resultado.filas().count());
    }

    @Given("que existe un servicio sin recurso y con un horario disponible")
    public void prepararReservaSinRecurso() {
        prepararHorarioSinRecurso();
    }

    @Given("que existe un servicio con un recurso activo y disponible durante un horario")
    public void prepararReservaConRecurso() {
        datos.prepararHorario(48, true, true);
    }

    @When("el cliente reserva el horario")
    public void reservarHorario() {
        datos.confirmarReserva(datos.usuarios.cliente());
        resultado = datos.confirmacion;
    }

    @When("el cliente reserva ese horario")
    public void reservarConRecurso() {
        reservarHorario();
    }

    @Then("la reserva de servicio debe quedar confirmada")
    public void verificarReservaConfirmada() {
        assertEquals("confirmada", resultado.exige(201).campo("estado"));
        assertEquals(datos.horarioId, resultado.campo("horarioId"));
        assertEquals(datos.servicioId, resultado.campo("servicioId"));
    }

    @Then("la reserva debe quedar confirmada con el recurso asociado")
    public void verificarRecursoDeReserva() {
        verificarReservaConfirmada();
        assertEquals(datos.recursoId, resultado.campo("recursoId"));
    }

    @Then("el horario debe dejar de estar disponible")
    public void verificarHorarioNoDisponible() {
        assertFalse(datos.disponibilidad().filas().anyMatch(fila -> datos.horarioId.equals(fila.path("id").asText())));
    }

    @Then("el horario debe dejar de estar disponible para otros clientes")
    public void verificarHorarioNoDisponibleParaOtros() {
        verificarHorarioNoDisponible();
    }

    @Given("que existe un horario con una reserva confirmada")
    public void prepararHorarioOcupado() {
        prepararHorarioSinRecurso();
        reservarHorario();
    }

    @When("el cliente intenta reservar el mismo horario")
    public void intentarReservarHorarioOcupado() {
        resultado = datos.reservar(datos.usuarios.nuevoCliente());
    }

    @Then("la nueva reserva debe rechazarse por falta de disponibilidad")
    public void verificarOcupacionRechazada() {
        assertTrue(resultado.exige(409).campo("error").contains("no está disponible"));
    }

    @Then("la reserva existente debe permanecer sin cambios")
    public void verificarReservaIntacta() {
        verificarEstadoEnHistorial("confirmada");
    }

    @Given("que existe un servicio cuyo recurso no cubre el período del horario")
    public void prepararRecursoNoDisponible() {
        datos.prepararHorario(48, true, false);
    }

    @When("el cliente intenta reservar ese horario")
    public void intentarReservarSinRecurso() {
        resultado = datos.reservar(datos.usuarios.cliente());
    }

    @Then("la reserva debe rechazarse por falta de disponibilidad del recurso")
    public void verificarRecursoNoDisponible() {
        assertTrue(resultado.exige(409).campo("error").contains("recurso requerido no está disponible"));
    }

    @Then("no debe crearse una nueva reserva")
    public void verificarQueNoSeCreoReserva() {
        ApiReservas.Respuesta historial = historialCliente();
        if (historial.json().isArray()) {
            assertFalse(historial.filas().anyMatch(fila -> datos.horarioId.equals(fila.path("horarioId").asText())));
        } else {
            assertTrue(historial.json().hasNonNull("mensaje"));
        }
    }

    @Given("que el cliente tiene una reserva confirmada que inicia en veinticuatro horas")
    public void prepararCancelacionAnticipada() {
        datos.prepararHorario(24, true, true);
        reservarHorario();
    }

    @Given("que el cliente tiene una reserva confirmada que inicia en seis horas")
    public void prepararCancelacionProxima() {
        datos.prepararHorario(6, false, false);
        reservarHorario();
    }

    @When("cancela la reserva")
    public void cancelarReservaDeServicio() {
        resultado = datos.cancelar(datos.usuarios.cliente());
    }

    @When("intenta cancelar la reserva")
    public void intentarCancelarReservaDeServicio() {
        cancelarReservaDeServicio();
    }

    @Then("la reserva de servicio debe quedar cancelada")
    public void verificarCancelacionDelServicio() {
        assertEquals(datos.reservaId, resultado.exige(200).id());
        assertEquals("cancelada", resultado.campo("estado"));
        verificarEstadoEnHistorial("cancelada");
    }

    @Then("el horario y el recurso deben quedar nuevamente disponibles durante ese período")
    public void verificarLiberacionDeRecursoYHorario() {
        datos.disponibilidad().fila("id", datos.horarioId);
        ApiReservas.Respuesta nueva = datos.reservar(datos.usuarios.nuevoCliente()).exige(201);
        assertEquals(datos.recursoId, nueva.campo("recursoId"));
    }

    @Then("la cancelación del servicio debe rechazarse por el plazo mínimo de doce horas")
    public void verificarRechazoPorPlazo() {
        assertEquals(409, resultado.estado());
        assertTrue(resultado.texto().contains("12"));
    }

    @Then("la reserva de servicio debe permanecer confirmada")
    public void verificarEstadoSinCambios() {
        verificarEstadoEnHistorial("confirmada");
    }

    @Given("que el administrador registra un recurso activo con disponibilidad futura")
    public void crearRecursoFuturo() {
        datos.asignarPeriodo(48);
        datos.crearRecurso();
        datos.definirDisponibilidad().exige(201);
    }

    @Given("registra un servicio único que requiere ese recurso")
    public void crearServicioConElRecurso() {
        datos.crearServicio();
    }

    @Given("registra un proveedor y lo asigna al servicio")
    public void asignarProveedorNuevo() {
        datos.asignarProveedor().exige(201);
    }

    @Given("el proveedor inicia sesión y publica un horario cubierto por la disponibilidad del recurso")
    public void publicarHorarioDeFlujo() {
        datos.horarioId = datos.publicarHorario(datos.inicio, datos.fin).exige(201).id();
    }

    @When("un cliente activo inicia sesión y reserva el horario disponible")
    public void reservarComoClienteEnElFlujo() {
        iniciarSesionEnPantalla(Configuracion.usuarioCliente(), Configuracion.claveCliente());
        reservarEnPantalla(datos.usuarios.cliente());
    }

    @Then("la reserva debe quedar confirmada con el recurso asignado")
    public void verificarRecursoDelFlujo() {
        assertEquals("confirmada", reservaDelFlujo.path("estado").asText());
        assertEquals(datos.servicioId, reservaDelFlujo.path("servicioId").asText());
        assertEquals(datos.recursoId, reservaDelFlujo.path("recursoId").asText());
    }

    @Then("el horario no debe continuar disponible para otros clientes")
    public void verificarOcupacionDelFlujo() {
        verificarHorarioNoDisponible();
    }

    @Then("la reserva debe aparecer en los historiales del cliente, proveedor y negocio")
    public void verificarHistorialesDelFlujo() {
        verificarEstadoEnHistorial("confirmada");
        JsonNode proveedor = datos.proveedor().get("/api/reservas/historial-proveedor").exige(200).fila("reservaId", datos.reservaId);
        assertEquals("confirmada", proveedor.path("estado").asText());
        JsonNode negocio = datos.usuarios.admin().get("/api/reservas/historial-negocio").exige(200).fila("reservaId", datos.reservaId);
        assertEquals("confirmada", negocio.path("estado").asText());
        assertEquals("SERVICIO", negocio.path("tipo").asText());
    }

    @Then("los reportes de uso y ocupación deben incluir la nueva reserva con valores consistentes")
    public void verificarReportesDelFlujo() {
        LocalDateTime creada = OffsetDateTime.parse(reservaDelFlujo.path("creadaEn").asText()).toLocalDateTime();
        JsonNode uso = datos.usuarios.admin().get("/api/servicios/uso?desde=" + creada.minusMinutes(1)
                + "&hasta=" + creada.plusMinutes(1)).exige(200).fila("servicioId", datos.servicioId);
        assertEquals(1, uso.path("reservas").asInt(-1));
        JsonNode ocupacion = datos.usuarios.admin().get("/api/reservas/ocupacion-servicios?desde=" + datos.inicio.minusMinutes(1)
                + "&hasta=" + datos.fin.plusMinutes(1)).exige(200).fila("servicioId", datos.servicioId);
        assertEquals(1, ocupacion.path("horariosReservados").asInt(-1));
        assertEquals(1, ocupacion.path("totalHorarios").asInt(-1));
        assertEquals(100.0, ocupacion.path("porcentajeOcupacion").asDouble(-1), 0.01);
    }

    @Given("que el administrador y el proveedor prepararon un servicio único con un horario disponible")
    public void prepararHorarioParaElClienteNuevo() {
        prepararHorarioSinRecurso();
    }

    @When("una persona se registra como cliente utilizando datos válidos y un correo único")
    public void registrarClienteNuevo() {
        clienteNuevo = new ContextoEscenario().clienteNuevo();
        theActorCalled("Cliente").attemptsTo(RegistrarCliente.con(clienteNuevo),
                WaitUntil.the(TITULO_LOGIN, isVisible()).forNoMoreThan(45).seconds());
    }

    @When("inicia sesión con las credenciales recién registradas")
    public void iniciarSesionConClienteNuevo() {
        iniciarSesionEnPantalla(clienteNuevo.correo(), clienteNuevo.clave());
    }

    @When("consulta el servicio y reserva el horario disponible")
    public void reservarPrimeraCita() {
        datos.disponibilidad().fila("id", datos.horarioId);
        reservarEnPantalla(ApiReservas.iniciarSesion(clienteNuevo.correo(), clienteNuevo.clave()));
    }

    @Then("la primera reserva queda confirmada en el sistema")
    public void verificarConfirmacionPrimeraReserva() {
        assertEquals("confirmada", reservaDelFlujo.path("estado").asText());
    }

    @Then("la reserva debe aparecer confirmada en el historial del nuevo cliente")
    public void verificarHistorialDelClienteNuevo() {
        Target fila = Target.the("primera reserva confirmada en el historial")
                .locatedBy("//tr[td[normalize-space(.)='{0}'] and td[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + " 'abcdefghijklmnopqrstuvwxyz'), 'confirmada')]]").of(datos.servicioNombre);
        theActorInTheSpotlight().attemptsTo(AbrirRuta.de("/historial/"),
                WaitUntil.the(fila, isVisible()).forNoMoreThan(45).seconds());
    }

    private void iniciarSesionEnPantalla(String correo, String clave) {
        theActorCalled("Cliente").attemptsTo(IniciarSesion.con(correo, clave),
                WaitUntil.the(CERRAR_SESION, isVisible()).forNoMoreThan(45).seconds());
    }

    private void reservarEnPantalla(ApiReservas usuario) {
        theActorInTheSpotlight().attemptsTo(ReservarEnPantalla.servicio(datos.servicioId,
                datos.horarioId, datos.servicioNombre));
        reservaDelFlujo = usuario.get("/api/reservas/historial").exige(200).fila("horarioId", datos.horarioId);
        datos.reservaId = reservaDelFlujo.path("reservaId").asText();
    }

    private ApiReservas.Respuesta historialCliente() {
        return datos.usuarios.cliente().get("/api/reservas/historial").exige(200);
    }

    private void verificarEstadoEnHistorial(String estado) {
        JsonNode fila = historialCliente().fila("reservaId", datos.reservaId);
        assertEquals(estado, fila.path("estado").asText());
        assertEquals(datos.horarioId, fila.path("horarioId").asText());
    }
}
