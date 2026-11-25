package co.com.eap05.certificacion.reservas.tasks;

import co.com.eap05.certificacion.reservas.utils.Configuracion;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class AbrirRuta implements Task {
    private final String ruta;

    public AbrirRuta(String ruta) {
        this.ruta = ruta;
    }

    public static AbrirRuta de(String ruta) {
        return instrumented(AbrirRuta.class, ruta);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String base = Configuracion.urlBase().replaceAll("/+$", "");
        String destino = ruta.startsWith("/") ? ruta : "/" + ruta;
        actor.attemptsTo(Open.url(base + destino));
    }
}
