package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.CERRAR_SESION;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CerrarSesion implements Task {
    public static CerrarSesion ahora() {
        return instrumented(CerrarSesion.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(CERRAR_SESION));
    }
}
