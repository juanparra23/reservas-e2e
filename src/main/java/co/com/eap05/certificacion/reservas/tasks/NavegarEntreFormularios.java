package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.targets.Target;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.ENLACE_LOGIN;
import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.ENLACE_REGISTRO;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class NavegarEntreFormularios implements Task {
    private final Target enlace;

    public NavegarEntreFormularios(Target enlace) {
        this.enlace = enlace;
    }

    public static NavegarEntreFormularios alRegistro() {
        return instrumented(NavegarEntreFormularios.class, ENLACE_REGISTRO);
    }

    public static NavegarEntreFormularios alInicioDeSesion() {
        return instrumented(NavegarEntreFormularios.class, ENLACE_LOGIN);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Scroll.to(enlace).andAlignToTop(), Click.on(enlace));
    }
}
