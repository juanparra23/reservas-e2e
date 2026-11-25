package co.com.eap05.certificacion.reservas.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;

public final class ValidacionDelCampo {
    private ValidacionDelCampo() {
    }

    public static Question<Boolean> faltaUnValorEn(Target campo) {
        return indica(campo, "valueMissing");
    }

    public static Question<Boolean> tieneLongitudInsuficiente(Target campo) {
        return indica(campo, "tooShort");
    }

    private static Question<Boolean> indica(Target campo, String restriccion) {
        return Question.about(campo.getName() + ": " + restriccion).answeredBy(actor ->
                Boolean.TRUE.equals(((JavascriptExecutor) BrowseTheWeb.as(actor).getDriver())
                        .executeScript("return arguments[0].validity[arguments[1]];",
                                campo.resolveFor(actor).getWrappedElement(), restriccion)));
    }
}
