package co.com.eap05.certificacion.reservas.utils;

public final class Configuracion {
    private static final String URL_POR_DEFECTO = "https://reservas-front-eap05.vercel.app/";

    private Configuracion() {
    }

    public static String urlBase() {
        return System.getProperty("reservas.base.url", URL_POR_DEFECTO);
    }

    public static String usuarioAdministrador() {
        return propiedadObligatoria("reservas.admin.email");
    }

    public static String claveAdministrador() {
        return propiedadObligatoria("reservas.admin.password");
    }

    public static String usuarioProveedor() {
        return propiedadObligatoria("reservas.proveedor.email");
    }

    public static String claveProveedor() {
        return propiedadObligatoria("reservas.proveedor.password");
    }

    public static String usuarioCliente() {
        return propiedadObligatoria("reservas.cliente.email");
    }

    public static String claveCliente() {
        return propiedadObligatoria("reservas.cliente.password");
    }

    private static String propiedadObligatoria(String nombre) {
        String valor = System.getProperty(nombre);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(nombre.toUpperCase().replace('.', '_'));
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta configurar -D" + nombre
                    + " o la variable " + nombre.toUpperCase().replace('.', '_'));
        }
        return valor;
    }
}
