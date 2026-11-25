package co.com.eap05.certificacion.reservas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class ComponentesComunes {
    public static Target controlConTexto(String texto) {
        String seguro = texto.replace("'", "");
        return Target.the("control " + seguro).located(By.xpath(
                "//*[self::button or self::a or @role='button'][contains(translate(normalize-space(.),'ÁÉÍÓÚABCDEFGHIJKLMNOPQRSTUVWXYZ','AEIOUabcdefghijklmnopqrstuvwxyz'),'"
                        + seguro.toLowerCase() + "')]"));
    }

    public static Target campo(String nombre) {
        String seguro = nombre.replace("'", "");
        return Target.the("campo " + seguro).located(By.xpath(
                "(//input[contains(translate(@id,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + seguro.toLowerCase() + "')"
                        + " or contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + seguro.toLowerCase() + "')"
                        + " or contains(translate(@placeholder,'ÁÉÍÓÚABCDEFGHIJKLMNOPQRSTUVWXYZ','AEIOUabcdefghijklmnopqrstuvwxyz'),'" + seguro.toLowerCase() + "')])[1]"));
    }

    private ComponentesComunes() {
    }
}
