package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.tasks.IniciarSesion;
import co.com.eap05.certificacion.reservas.tasks.RegistrarEnCatalogo;
import co.com.eap05.certificacion.reservas.utils.Configuracion;
import co.com.eap05.certificacion.reservas.utils.DatosUnicos;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.eap05.certificacion.reservas.userinterfaces.Catalogo.CONFIRMACION;
import static co.com.eap05.certificacion.reservas.userinterfaces.Catalogo.REGISTRO;
import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.CERRAR_SESION;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.Text.of;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.Matchers.matchesPattern;

public class CatalogoStepDefinitions {
    private static final String TEXTO_CON_UUID = "(?is).*\\b[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}\\b.*";
    private static final int DURACION_MINUTOS = 45;
    private static final String DESCRIPCION = "Registro generado por la suite de QA";
    private String nombre;

    @Given("que el administrador inició sesión")
    public void iniciarSesionDeAdministrador() {
        theActorCalled("Administrador").wasAbleTo(
                IniciarSesion.con(Configuracion.usuarioAdministrador(), Configuracion.claveAdministrador()),
                WaitUntil.the(CERRAR_SESION, isVisible()).forNoMoreThan(45).seconds());
    }

    @Given("que el administrador dispone de datos únicos y válidos para un servicio")
    public void prepararServicio() {
        nombre = DatosUnicos.nombre("Servicio QA");
    }

    @Given("que el administrador dispone de un nombre único y una descripción válida")
    public void prepararRecurso() {
        nombre = DatosUnicos.nombre("Recurso QA");
    }

    @When("registra el servicio sin asociar un recurso")
    public void registrarServicio() {
        theActorInTheSpotlight().attemptsTo(RegistrarEnCatalogo.servicio(nombre, DESCRIPCION, DURACION_MINUTOS));
    }

    @When("registra el recurso")
    public void registrarRecurso() {
        theActorInTheSpotlight().attemptsTo(RegistrarEnCatalogo.recurso(nombre, DESCRIPCION));
    }

    @Then("el catálogo debe confirmar el registro del servicio")
    public void confirmarServicio() {
        verificarConfirmacion("Servicio");
    }

    @Then("el catálogo debe confirmar el registro del recurso")
    public void confirmarRecurso() {
        verificarConfirmacion("Recurso");
    }

    @Then("debe listar el servicio con el nombre proporcionado y un identificador")
    public void verificarServicioListado() {
        verificarRegistro("servicio");
    }

    @Then("debe listar el recurso con el nombre proporcionado y un identificador")
    public void verificarRecursoListado() {
        verificarRegistro("recurso");
    }

    private void verificarRegistro(String tipo) {
        Target registro = REGISTRO.of(nombre, tipo);
        theActorInTheSpotlight().should(
                seeThat(the(registro), isVisible()),
                seeThat(of(registro), matchesPattern(TEXTO_CON_UUID)));
    }

    private void verificarConfirmacion(String tipo) {
        Target mensaje = CONFIRMACION.of(tipo + " “" + nombre + "” registrado.");
        theActorInTheSpotlight().attemptsTo(WaitUntil.the(mensaje, isVisible()).forNoMoreThan(30).seconds());
        theActorInTheSpotlight().should(seeThat(the(mensaje), isVisible()));
    }
}
