package co.com.eap05.certificacion.reservas.utils;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/** Datos aislados por escenario para evitar colisiones entre ejecuciones. */
public final class DatosUnicos {
    private static final AtomicInteger SECUENCIA = new AtomicInteger();

    private DatosUnicos() {
    }

    public static String sufijo() {
        return Instant.now().toEpochMilli() + "-" + SECUENCIA.incrementAndGet();
    }

    public static String correoCliente() {
        return "e2e.cliente." + sufijo() + "@example.com";
    }

    public static String nombre(String prefijo) {
        return prefijo + "-" + sufijo();
    }
}

