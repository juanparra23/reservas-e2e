package co.com.eap05.certificacion.reservas.tasks;

import co.com.eap05.certificacion.reservas.model.Cliente;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;

import static co.com.eap05.certificacion.reservas.userinterfaces.FormularioAutenticacion.*;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RegistrarCliente implements Task {
    private final Cliente cliente;

    public RegistrarCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public static RegistrarCliente con(Cliente cliente) {
        return instrumented(RegistrarCliente.class, cliente);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                AbrirRuta.de("/registro/"),
                Enter.theValue(cliente.nombre()).into(NOMBRE),
                Enter.theValue(cliente.correo()).into(CORREO),
                Enter.theValue(cliente.telefono()).into(TELEFONO),
                Enter.theValue(cliente.direccion()).into(DIRECCION)
        );
        CLAVE.resolveFor(actor).sendKeys(cliente.clave());
        actor.attemptsTo(Click.on(CREAR_CUENTA));
    }
}
