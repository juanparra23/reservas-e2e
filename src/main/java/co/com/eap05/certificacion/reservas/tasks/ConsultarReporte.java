package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static co.com.eap05.certificacion.reservas.userinterfaces.Reportes.*;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ConsultarReporte implements Task {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");
    private final String ruta;
    private final LocalDateTime desde;
    private final LocalDateTime hasta;

    public ConsultarReporte(String ruta, LocalDateTime desde, LocalDateTime hasta) {
        this.ruta = ruta;
        this.desde = desde;
        this.hasta = hasta;
    }

    public static ConsultarReporte uso(LocalDateTime desde, LocalDateTime hasta) {
        return instrumented(ConsultarReporte.class, "/servicios/uso/", desde, hasta);
    }

    public static ConsultarReporte ocupacion(LocalDateTime desde, LocalDateTime hasta) {
        return instrumented(ConsultarReporte.class, "/servicios/ocupacion/", desde, hasta);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(AbrirRuta.de(ruta));
        seleccionarFecha(actor, DESDE, desde);
        seleccionarFecha(actor, HASTA, hasta);
        actor.attemptsTo(Click.on(CONSULTAR));
    }

    private static void seleccionarFecha(Actor actor, Target campo, LocalDateTime fecha) {
        // El selector nativo de fecha cambia con el idioma de Chrome.
        ((JavascriptExecutor) BrowseTheWeb.as(actor).getDriver()).executeScript("""
                const input = arguments[0];
                Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set.call(input, arguments[1]);
                input.dispatchEvent(new Event('input', { bubbles: true }));
                input.dispatchEvent(new Event('change', { bubbles: true }));
                """, campo.resolveFor(actor).getWrappedElement(), fecha.format(FORMATO));
    }
}
