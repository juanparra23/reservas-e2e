package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.tasks.ConsultarReporte;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.time.LocalDateTime;

import static co.com.eap05.certificacion.reservas.userinterfaces.Reportes.MENSAJE;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

public class ReportesStepDefinitions {
    private LocalDateTime desde;
    private LocalDateTime hasta;

    @Given("que el administrador selecciona un período futuro sin datos de prueba")
    public void prepararPeriodoSinDatos() {
        desde = LocalDateTime.now().plusYears(50).withHour(12).withMinute(0);
        hasta = desde.plusDays(1);
    }

    @Given("que el administrador selecciona una fecha inicial posterior a la fecha final")
    public void prepararPeriodoInvertido() {
        hasta = LocalDateTime.now().withHour(12).withMinute(0);
        desde = hasta.plusDays(1);
    }

    @When("solicita el reporte de uso para ese período")
    public void consultarUso() {
        theActorInTheSpotlight().attemptsTo(ConsultarReporte.uso(desde, hasta));
    }

    @When("solicita el reporte de ocupación para ese período")
    @When("solicita el reporte de ocupación de servicios")
    public void consultarOcupacion() {
        theActorInTheSpotlight().attemptsTo(ConsultarReporte.ocupacion(desde, hasta));
    }

    @Then("debe informarse que no existen datos de uso")
    public void verificarUsoVacio() {
        verificarMensaje("Sin datos de uso");
    }

    @Then("debe informarse que no existen datos de ocupación")
    public void verificarOcupacionVacia() {
        verificarMensaje("Sin datos de ocupación");
    }

    @Then("debe mostrarse que la fecha inicial debe ser anterior a la fecha final")
    public void verificarPeriodoInvalido() {
        verificarMensaje("La fecha «hasta» debe ser posterior a «desde».");
    }

    private void verificarMensaje(String texto) {
        Target mensaje = MENSAJE.of(texto);
        theActorInTheSpotlight().attemptsTo(WaitUntil.the(mensaje, isVisible()).forNoMoreThan(30).seconds());
        theActorInTheSpotlight().should(seeThat(the(mensaje), isVisible()));
    }
}
