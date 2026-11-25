package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.model.Cliente;
import co.com.eap05.certificacion.reservas.model.ContextoEscenario;
import co.com.eap05.certificacion.reservas.questions.ValidacionDelCampo;
import co.com.eap05.certificacion.reservas.tasks.AbrirRuta;
import co.com.eap05.certificacion.reservas.tasks.CerrarSesion;
import co.com.eap05.certificacion.reservas.tasks.EditarCampoDeRegistro;
import co.com.eap05.certificacion.reservas.tasks.IniciarSesion;
import co.com.eap05.certificacion.reservas.tasks.NavegarEntreFormularios;
import co.com.eap05.certificacion.reservas.tasks.RegistrarCliente;
import co.com.eap05.certificacion.reservas.utils.Configuracion;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.*;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static org.hamcrest.Matchers.is;

public class AutenticacionStepDefinitions {
    private final ContextoEscenario contexto = new ContextoEscenario();

    @Given("que una persona no tiene una sesión activa")
    public void prepararVisitante() {
        theActorCalled("Visitante");
    }

    @When("intenta abrir directamente su historial")
    public void abrirHistorial() {
        theActorInTheSpotlight().attemptsTo(AbrirRuta.de("/historial/"));
    }

    @Then("debe ver que necesita iniciar sesión para consultar sus reservas")
    public void verificarHistorialPrivado() {
        theActorInTheSpotlight().should(seeThat(the(AVISO_HISTORIAL_PRIVADO), isVisible()));
    }

    @Given("que una persona utiliza datos personales válidos y un correo único")
    public void prepararRegistro() {
        abrirRegistro();
    }

    @When("registra una cuenta de cliente")
    public void registrarCliente() {
        theActorInTheSpotlight().attemptsTo(RegistrarCliente.con(contexto.clienteNuevo()));
    }

    @Then("puede iniciar sesión con la cuenta creada")
    public void iniciarSesionConLaCuentaNueva() {
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(TITULO_LOGIN, isVisible()).forNoMoreThan(45).seconds());
        verificarFormularioDeAcceso();
        Cliente cliente = contexto.clienteNuevo();
        theActorInTheSpotlight().attemptsTo(IniciarSesion.con(cliente.correo(), cliente.clave()));
        verificarAcceso();
    }

    @Given("que existe una cuenta asociada a un correo")
    public void prepararCorreoExistente() {
        abrirRegistro();
    }

    @When("otra persona intenta registrarse utilizando el mismo correo")
    public void intentarRegistrarCorreoExistente() {
        Cliente nuevo = contexto.clienteNuevo();
        Cliente duplicado = new Cliente(nuevo.nombre(), Configuracion.usuarioCliente(),
                nuevo.telefono(), nuevo.direccion(), nuevo.clave());
        theActorInTheSpotlight().attemptsTo(RegistrarCliente.con(duplicado));
    }

    @Then("debe mostrarse que el correo ya está registrado")
    public void verificarCorreoDuplicado() {
        theActorInTheSpotlight().should(seeThat(the(CORREO_DUPLICADO), isVisible()));
    }

    @Then("el visitante debe permanecer en el formulario de registro")
    public void verificarPermanenciaEnRegistro() {
        verificarFormularioDeRegistro();
    }

    @Given("que existe un usuario activo")
    public void prepararCuentaDePrueba() {
        prepararCliente();
    }

    @When("intenta iniciar sesión con una contraseña incorrecta")
    public void intentarInicioConClaveIncorrecta() {
        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.con(Configuracion.usuarioCliente(), java.util.UUID.randomUUID().toString()));
    }

    @Then("debe mostrarse un mensaje de credenciales inválidas")
    public void verificarCredencialesInvalidas() {
        theActorInTheSpotlight().should(seeThat(the(CREDENCIALES_INVALIDAS), isVisible()));
    }

    @When("deja vacío el campo de registro {string}")
    public void dejarCampoVacio(String campo) {
        theActorInTheSpotlight().attemptsTo(EditarCampoDeRegistro.dejarVacio(campo));
    }

    @Then("el navegador debe invalidar el campo de registro {string} por estar vacío")
    public void verificarCampoObligatorio(String campo) {
        theActorInTheSpotlight().should(
                seeThat(ValidacionDelCampo.faltaUnValorEn(campoDeRegistro(campo)), is(true)));
    }

    @When("escribe una contraseña de cinco caracteres en el registro")
    public void escribirContrasenaCorta() {
        theActorInTheSpotlight().attemptsTo(EditarCampoDeRegistro.contrasenaCorta());
    }

    @Then("el formulario debe indicar que la contraseña requiere mínimo seis caracteres")
    public void verificarAyudaDeContrasena() {
        theActorInTheSpotlight().should(seeThat(the(AYUDA_CLAVE), isVisible()));
    }

    @Then("el navegador debe invalidar la contraseña por su longitud")
    public void verificarLongitudDeContrasena() {
        theActorInTheSpotlight().should(seeThat(ValidacionDelCampo.tieneLongitudInsuficiente(CLAVE), is(true)));
    }

    @Given("que el visitante abre el inicio de sesión")
    public void abrirInicioDeSesion() {
        theActorCalled("Visitante").wasAbleTo(AbrirRuta.de("/login/"));
    }

    @Given("que el visitante abre el registro de clientes")
    public void abrirRegistro() {
        theActorCalled("Visitante").wasAbleTo(AbrirRuta.de("/registro/"));
    }

    @When("elige registrarse como cliente")
    public void irAlRegistro() {
        theActorInTheSpotlight().attemptsTo(NavegarEntreFormularios.alRegistro());
    }

    @When("elige iniciar sesión desde el registro")
    public void irAlInicioDeSesion() {
        theActorInTheSpotlight().attemptsTo(NavegarEntreFormularios.alInicioDeSesion());
    }

    @Then("debe ver el formulario de registro del cliente")
    public void verificarFormularioDeRegistro() {
        theActorInTheSpotlight().should(
                seeThat(the(TITULO_REGISTRO), isVisible()),
                seeThat(the(NOMBRE), isVisible()),
                seeThat(the(CORREO), isVisible()),
                seeThat(the(TELEFONO), isVisible()),
                seeThat(the(DIRECCION), isVisible()),
                seeThat(the(CLAVE), isVisible()),
                seeThat(the(CREAR_CUENTA), isVisible())
        );
    }

    @Then("debe ver el formulario de inicio de sesión")
    @Then("debe regresar a la pantalla de inicio de sesión")
    public void verificarFormularioDeAcceso() {
        theActorInTheSpotlight().should(
                seeThat(the(TITULO_LOGIN), isVisible()),
                seeThat(the(CORREO), isVisible()),
                seeThat(the(CLAVE), isVisible()),
                seeThat(the(INICIAR_SESION), isVisible())
        );
    }

    @Given("que existe un usuario activo con credenciales válidas")
    public void prepararCliente() {
        theActorCalled("Cliente").wasAbleTo(AbrirRuta.de("/login/"));
    }

    @Given("que un usuario tiene una sesión activa")
    public void prepararSesionActiva() {
        prepararCliente();
        iniciarSesion();
        verificarAcceso();
    }

    @When("inicia sesión con su correo y contraseña")
    public void iniciarSesion() {
        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.con(Configuracion.usuarioCliente(), Configuracion.claveCliente())
        );
    }

    @When("cierra su sesión")
    public void cerrarSesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.ahora());
    }

    @Then("debe acceder a la plataforma")
    public void verificarAcceso() {
        theActorInTheSpotlight().should(seeThat(the(CERRAR_SESION), isVisible()));
    }

    @Then("debe visualizar las opciones correspondientes a su rol")
    public void verificarOpcionDelCliente() {
        theActorInTheSpotlight().should(seeThat(the(HISTORIAL_CLIENTE), isVisible()));
    }

    @Then("no debe acceder a una página protegida con la sesión anterior")
    public void verificarCierreDeSesion() {
        abrirHistorial();
        verificarHistorialPrivado();
    }
}
