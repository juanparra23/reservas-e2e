package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.model.Cliente;
import co.com.eap05.certificacion.reservas.model.ContextoEscenario;
import co.com.eap05.certificacion.reservas.utils.Configuracion;

import java.util.Map;

final class UsuariosDePrueba {
    private ApiReservas admin;
    private ApiReservas cliente;
    private ApiReservas proveedor;

    ApiReservas admin() {
        if (admin == null) {
            admin = ApiReservas.iniciarSesion(Configuracion.usuarioAdministrador(), Configuracion.claveAdministrador());
        }
        return admin;
    }

    ApiReservas cliente() {
        if (cliente == null) {
            cliente = ApiReservas.iniciarSesion(Configuracion.usuarioCliente(), Configuracion.claveCliente());
        }
        return cliente;
    }

    ApiReservas proveedor() {
        if (proveedor == null) {
            proveedor = ApiReservas.iniciarSesion(Configuracion.usuarioProveedor(), Configuracion.claveProveedor());
        }
        return proveedor;
    }

    ApiReservas nuevoCliente() {
        Cliente datos = new ContextoEscenario().clienteNuevo();
        ApiReservas.publica().post("/api/usuario/registrar", datosDeRegistro(datos)).exige(201);
        return ApiReservas.iniciarSesion(datos.correo(), datos.clave());
    }

    static Map<String, String> datosDeRegistro(Cliente datos) {
        return Map.of("nombre", datos.nombre(), "email", datos.correo(), "telefono", datos.telefono(),
                "direccion", datos.direccion(), "contrasena", datos.clave());
    }
}
