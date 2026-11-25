package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.model.ContextoEscenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PermisosStepDefinitions {
    private ApiReservas cliente;
    private ApiReservas.Respuesta respuesta;

    @Given("que un cliente de QA está autenticado en la API")
    public void autenticarCliente() {
        cliente = new UsuariosDePrueba().cliente();
    }

    @When("solicita registrar un proveedor con datos válidos")
    public void solicitarCreacion() {
        respuesta = cliente.post("/api/usuario/registrar/proveedor",
                UsuariosDePrueba.datosDeRegistro(new ContextoEscenario().clienteNuevo()));
    }

    @Then("la API rechaza el registro de proveedor con HTTP {int}")
    public void verificarPermiso(int estado) {
        respuesta.exige(estado);
    }
}
