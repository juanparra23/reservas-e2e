package co.com.eap05.certificacion.reservas.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/autenticacion_registro.feature",
        glue = "co.com.eap05.certificacion.reservas.stepdefinitions",
        tags = "@cuenta_prueba and not @pendiente",
        plugin = {"pretty"}
)
public class AutenticacionRunner {
}
