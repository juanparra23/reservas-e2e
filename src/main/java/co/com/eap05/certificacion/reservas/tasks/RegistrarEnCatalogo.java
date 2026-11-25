package co.com.eap05.certificacion.reservas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Scroll;
import net.serenitybdd.screenplay.targets.Target;

import static co.com.eap05.certificacion.reservas.userinterfaces.Catalogo.*;

public final class RegistrarEnCatalogo {
    private RegistrarEnCatalogo() {
    }

    public static Performable servicio(String nombre, String descripcion, int minutos) {
        return Task.where("{0} registra un servicio sin recurso",
                AbrirRuta.de("/servicios/"),
                Enter.theValue(nombre).into(NOMBRE_SERVICIO),
                Enter.theValue(descripcion).into(DESCRIPCION_SERVICIO),
                Enter.theValue(Integer.toString(minutos)).into(DURACION),
                enviar(REGISTRAR_SERVICIO));
    }

    public static Performable recurso(String nombre, String descripcion) {
        return Task.where("{0} registra un recurso",
                AbrirRuta.de("/servicios/"),
                Click.on(PESTANA_RECURSO),
                Enter.theValue(nombre).into(NOMBRE_RECURSO),
                Enter.theValue(descripcion).into(DESCRIPCION_RECURSO),
                enviar(REGISTRAR_RECURSO));
    }

    private static Performable enviar(Target boton) {
        return Task.where("{0} envía el formulario del catálogo",
                Scroll.to(boton).andAlignToTop(), Click.on(boton));
    }
}
