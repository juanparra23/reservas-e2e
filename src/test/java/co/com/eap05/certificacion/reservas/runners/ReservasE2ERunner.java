package co.com.eap05.certificacion.reservas.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "co.com.eap05.certificacion.reservas.stepdefinitions",
        tags = "not @manual and not @pendiente and not @concurrencia and not @reloj_fijo",
        plugin = {"pretty"},
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class ReservasE2ERunner {
}

