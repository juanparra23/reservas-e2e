package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.targets.Target;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.campoDeRegistro;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class EditarCampoDeRegistro implements Task {
    private final Target campo;
    private final String valor;

    public EditarCampoDeRegistro(Target campo, String valor) {
        this.campo = campo;
        this.valor = valor;
    }

    public static EditarCampoDeRegistro dejarVacio(String campo) {
        return instrumented(EditarCampoDeRegistro.class, campoDeRegistro(campo), "");
    }

    public static EditarCampoDeRegistro contrasenaCorta() {
        return instrumented(EditarCampoDeRegistro.class, campoDeRegistro("contraseña"), "abc12");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        if (valor.isEmpty()) {
            actor.attemptsTo(Clear.field(campo));
        } else {
            actor.attemptsTo(Enter.theValue(valor).into(campo));
        }
    }
}
