package co.com.eap05.certificacion.reservas.stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

/** Elenco y navegador nuevos por escenario para preservar FIRST - Independent. */
public class Hooks {

    @Before
    public void prepararEscenario() {
        OnStage.setTheStage(new OnlineCast());
    }

    @After
    public void cerrarEscenario(Scenario escenario) {
        try {
            if (escenario.isFailed()) {
                Serenity.takeScreenshot();
            }
        } finally {
            OnStage.drawTheCurtain();
        }
    }
}

