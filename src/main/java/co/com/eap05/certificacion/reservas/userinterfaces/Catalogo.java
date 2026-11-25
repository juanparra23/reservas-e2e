package co.com.eap05.certificacion.reservas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class Catalogo {
    public static final Target PESTANA_RECURSO = Target.the("pestaña Recurso")
            .located(By.xpath("//button[@role='tab' and normalize-space()='Recurso']"));
    public static final Target NOMBRE_SERVICIO = Target.the("nombre del servicio").located(By.id("nombre"));
    public static final Target DESCRIPCION_SERVICIO = Target.the("descripción del servicio").located(By.id("descripcion"));
    public static final Target DURACION = Target.the("duración del servicio").located(By.id("duracionMin"));
    public static final Target NOMBRE_RECURSO = Target.the("nombre del recurso").located(By.id("recursoNombre"));
    public static final Target DESCRIPCION_RECURSO = Target.the("descripción del recurso").located(By.id("recursoDescripcion"));
    public static final Target REGISTRAR_SERVICIO = boton("Registrar servicio");
    public static final Target REGISTRAR_RECURSO = boton("Registrar recurso");
    public static final Target CONFIRMACION = Target.the("confirmación del catálogo")
            .locatedBy("//*[normalize-space(text())='{0}']");
    public static final Target REGISTRO = Target.the("registro del catálogo")
            .locatedBy("//li[.//*[normalize-space(text())='{0}'] and .//*[normalize-space(text())='{1}']]");

    private Catalogo() {
    }

    private static Target boton(String texto) {
        return Target.the(texto).located(By.xpath("//button[normalize-space()='" + texto + "']"));
    }
}
