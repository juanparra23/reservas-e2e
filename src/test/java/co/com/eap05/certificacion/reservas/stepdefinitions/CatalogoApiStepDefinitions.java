package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.utils.DatosUnicos;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.Assert.*;

public class CatalogoApiStepDefinitions {
    private final DatosDeReserva datos = new DatosDeReserva();
    private ApiReservas.Respuesta resultado;
    private String nombre;

    @Given("que existe un recurso activo")
    public void crearRecurso() {
        datos.crearRecurso();
    }

    @When("el administrador registra un servicio que requiere ese recurso")
    public void registrarServicioConRecurso() {
        resultado = datos.servicio(DatosUnicos.nombre("Servicio QA"), datos.recursoId);
    }

    @Then("el servicio debe quedar registrado")
    public void verificarServicioCreado() {
        resultado.exige(201).id();
        assertTrue(resultado.json().path("activo").asBoolean());
    }

    @Then("debe conservar la asociación con el recurso seleccionado")
    public void verificarRecursoAsociado() {
        assertEquals(datos.recursoId, resultado.campo("recursoId"));
    }

    @Given("que el administrador completa los demás datos válidos del servicio")
    public void prepararDatosDelServicio() {
        nombre = DatosUnicos.nombre("Servicio QA");
    }

    @When("intenta registrar el servicio sin nombre")
    public void registrarSinNombre() {
        resultado = datos.servicio("", null);
    }

    @Then("el servicio no debe crearse")
    public void verificarServicioNoCreado() {
        resultado.exige(400);
        assertFalse(resultado.json().hasNonNull("id"));
    }

    @Then("debe mostrarse que el nombre es obligatorio")
    public void verificarNombreObligatorio() {
        assertTrue(resultado.json().path("errores").path("nombre").asText().contains("obligatorio"));
    }

    @Given("que el administrador utiliza un identificador de recurso inexistente")
    public void prepararRecursoInexistente() {
        datos.recursoId = java.util.UUID.randomUUID().toString();
        prepararDatosDelServicio();
    }

    @When("intenta registrar un servicio que requiere ese recurso")
    public void registrarConRecursoInexistente() {
        resultado = datos.servicio(nombre, datos.recursoId);
    }

    @Then("debe mostrarse que el recurso debe existir y estar activo")
    public void verificarRecursoInexistente() {
        assertTrue(resultado.campo("error").contains("no existe"));
    }

    @Given("que existen un proveedor activo y un servicio registrado sin asociación entre ellos")
    public void crearServicioYProveedor() {
        datos.crearServicio();
        datos.crearProveedor();
    }

    @When("el administrador asigna el proveedor al servicio")
    public void asignarProveedor() {
        resultado = datos.asignarProveedor();
    }

    @Then("debe existir una sola asociación entre el proveedor y el servicio")
    public void verificarAsignacion() {
        resultado.exige(201).id();
        assertEquals(datos.proveedorId, resultado.campo("proveedorId"));
        assertEquals(datos.servicioId, resultado.campo("servicioId"));
        datos.asignarProveedor().exige(400);
    }

    @Given("que un proveedor ya está asignado a un servicio")
    public void prepararAsignacion() {
        datos.prepararServicioConProveedor();
    }

    @When("el administrador intenta asignar nuevamente el mismo proveedor al servicio")
    public void intentarDuplicarAsignacion() {
        asignarProveedor();
    }

    @Then("no debe crearse una asociación duplicada")
    public void verificarDuplicadoRechazado() {
        assertTrue(resultado.exige(400).texto().contains("ya se encuentra asignado"));
    }

    @Then("debe permanecer una sola asociación entre ambos")
    public void verificarAsociacionOriginal() {
        datos.asignarProveedor().exige(400);
    }

    @Given("que existe un servicio sin reservas asociadas")
    public void crearServicioParaEditar() {
        datos.crearServicio();
    }

    @When("el administrador modifica su nombre, descripción y duración con valores válidos")
    public void modificarServicio() {
        nombre = DatosUnicos.nombre("Servicio editado QA");
        resultado = datos.usuarios.admin().put("/api/servicios/" + datos.servicioId,
                Map.of("nombre", nombre, "descripcion", "Descripción editada QA", "duracionMin", 45));
    }

    @Then("los datos del servicio deben quedar actualizados")
    public void verificarDatosActualizados() {
        assertEquals(nombre, resultado.exige(200).campo("nombre"));
        assertEquals("Descripción editada QA", resultado.campo("descripcion"));
        assertEquals(45, resultado.json().path("duracionMin").asInt());
    }

    @Then("el identificador del servicio debe permanecer igual")
    public void verificarIdDelServicio() {
        assertEquals(datos.servicioId, resultado.id());
    }

    @Given("que el administrador completa los demás datos válidos del recurso")
    public void prepararDatosDeRecurso() {
        nombre = DatosUnicos.nombre("Recurso QA");
    }

    @When("intenta registrar el recurso sin proporcionar {string}")
    public void crearRecursoInvalido(String campo) {
        resultado = datos.recurso("nombre".equals(campo) ? "" : nombre,
                "descripción".equals(campo) ? "" : DatosDeReserva.DESCRIPCION);
    }

    @Then("el recurso no debe crearse")
    public void verificarRecursoNoCreado() {
        resultado.exige(400);
        assertFalse(resultado.json().hasNonNull("id"));
    }

    @Then("debe mostrarse que {string} es obligatorio")
    public void verificarCampoObligatorio(String campo) {
        String clave = campo.equals("descripción") ? "descripcion" : campo;
        assertTrue(resultado.json().path("errores").path(clave).isTextual());
    }

    @Given("que existe un recurso activo sin disponibilidad en el período seleccionado")
    public void prepararRecursoSinPeriodo() {
        datos.crearRecurso();
        datos.asignarPeriodo(48);
    }

    @When("el administrador define un inicio anterior al fin")
    public void definirPeríodo() {
        resultado = datos.definirDisponibilidad();
    }

    @Then("el período de disponibilidad debe quedar registrado")
    public void verificarPeriodo() {
        resultado.exige(201).id();
        assertEquals(datos.recursoId, resultado.campo("recursoId"));
    }

    @Then("debe conservar exactamente sus fechas de inicio y fin")
    public void verificarFechasDeRecurso() {
        assertEquals(datos.inicio.toInstant(ZoneOffset.UTC), OffsetDateTime.parse(resultado.campo("inicio")).toInstant());
        assertEquals(datos.fin.toInstant(ZoneOffset.UTC), OffsetDateTime.parse(resultado.campo("fin")).toInstant());
    }

    @Given("que existe un recurso activo asociado a una reserva confirmada que todavía no finaliza")
    public void crearRecursoComprometido() {
        datos.prepararHorario(48, true, true);
        datos.confirmarReserva(datos.usuarios.cliente());
    }

    @Given("que existe un recurso activo sin reservas confirmadas vigentes")
    public void prepararRecursoLibre() {
        datos.prepararHorario(48, true, true);
    }

    @When("el administrador intenta desactivar el recurso")
    public void intentarDesactivar() {
        desactivarRecurso();
    }

    @When("el administrador desactiva el recurso")
    public void desactivarRecurso() {
        resultado = datos.cambiarEstado(false);
    }

    @Then("la operación debe rechazarse porque el recurso está comprometido")
    public void verificarRecursoComprometido() {
        assertTrue(resultado.exige(409).campo("mensaje").contains("confirmada"));
    }

    @Then("el recurso debe permanecer activo")
    public void verificarRecursoPermaneceActivo() {
        datos.usuarios.admin().post("/api/servicios/validar-recurso", Map.of("recursoId", datos.recursoId)).exige(200);
    }

    @Then("el recurso debe quedar inactivo")
    public void verificarRecursoInactivo() {
        assertFalse(resultado.exige(200).json().path("activo").asBoolean(true));
    }

    @Then("no debe aparecer como disponible para nuevas reservas")
    public void verificarRecursoNoReservable() {
        ApiReservas.Respuesta intento = datos.reservar(datos.usuarios.cliente());
        assertTrue(intento.estado() == 409 || intento.estado() == 400);
        assertTrue(intento.texto().contains("recurso"));
    }
}
