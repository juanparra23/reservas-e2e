package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public final class ReservarEnPantalla {
    private ReservarEnPantalla() {
    }

    public static Performable servicio(String servicio, String horario, String nombre) {
        return Task.where("{0} reserva un horario",
                AbrirRuta.de("/reservas/"),
                Enter.theValue(servicio).into(campo("servicioId")),
                Enter.theValue(horario).into(campo("horarioId")),
                enviar("Confirmar reserva", "Reserva confirmada para “" + nombre + "”."));
    }

    public static Performable evento(String id) {
        return Task.where("{0} reserva un cupo",
                AbrirRuta.de("/eventos/reservar/"),
                Enter.theValue(id).into(campo("eventoId")),
                enviar("Confirmar cupo", "Cupo reservado correctamente."));
    }

    public static Performable cancelarEvento(String evento, String reserva) {
        return Task.where("{0} cancela la reserva del evento",
                Enter.theValue(evento).into(campo("cancelEventoId")),
                Enter.theValue(reserva).into(campo("cancelReservaId")),
                enviar("Cancelar reserva", "Reserva cancelada. El cupo vuelve a estar disponible."));
    }

    private static Target campo(String id) {
        return Target.the(id).locatedBy("#" + id);
    }

    private static Performable enviar(String boton, String mensaje) {
        Target enviar = Target.the(boton).locatedBy("//button[@type='submit' and normalize-space()='" + boton + "']");
        Target confirmacion = Target.the(mensaje).locatedBy("//main//*[normalize-space(text())='" + mensaje + "']");
        return Task.where("{0} envía el formulario y espera la confirmación",
                Scroll.to(enviar).andAlignToTop(), Click.on(enviar),
                WaitUntil.the(confirmacion, isVisible()).forNoMoreThan(45).seconds());
    }
}
