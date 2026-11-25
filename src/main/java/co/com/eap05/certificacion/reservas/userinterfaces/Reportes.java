package co.com.eap05.certificacion.reservas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class Reportes {
    public static final Target DESDE = Target.the("inicio del período").located(By.id("desde"));
    public static final Target HASTA = Target.the("fin del período").located(By.id("hasta"));
    public static final Target CONSULTAR = Target.the("botón de consulta del período")
            .located(By.xpath("//form[.//*[@id='desde'] and .//*[@id='hasta']]//button[@type='submit']"));
    public static final Target MENSAJE = Target.the("estado del reporte")
            .locatedBy("//main//*[normalize-space(text())='{0}']");

    private Reportes() {
    }
}
