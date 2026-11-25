package co.com.eap05.certificacion.reservas.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;

public class ContenidoVisible implements Question<String> {
    public static ContenidoVisible deLaPagina() {
        return new ContenidoVisible();
    }

    @Override
    public String answeredBy(Actor actor) {
        return BrowseTheWeb.as(actor).getDriver().findElement(By.tagName("body")).getText();
    }
}
