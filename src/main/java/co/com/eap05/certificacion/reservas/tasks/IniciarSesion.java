package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.*;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IniciarSesion implements Task {
    private final String correo;
    private final String clave;

    public IniciarSesion(String correo, String clave) {
        this.correo = correo;
        this.clave = clave;
    }

    public static IniciarSesion con(String correo, String clave) {
        return instrumented(IniciarSesion.class, correo, clave);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                AbrirRuta.de("/login/"),
                Enter.theValue(correo).into(CORREO)
        );
        // Escribir directamente evita incluir la contraseña en el nombre de un paso de Serenity.
        CLAVE.resolveFor(actor).sendKeys(clave);
        actor.attemptsTo(Click.on(INICIAR_SESION));
    }
}
