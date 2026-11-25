package co.com.eap05.certificacion.reservas.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "co.com.eap05.certificacion.reservas.stepdefinitions",
        tags = "@camino_alterno and not @pendiente and not @cuenta_prueba",
        plugin = {"pretty"},
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class CaminosAlternosRunner {
}
