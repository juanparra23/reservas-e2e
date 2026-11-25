package co.com.eap05.certificacion.reservas.model;

import co.com.eap05.certificacion.reservas.utils.DatosUnicos;

import java.util.UUID;

public final class ContextoEscenario {
    private Cliente cliente;

    public Cliente clienteNuevo() {
        if (cliente == null) {
            cliente = new Cliente(
                    DatosUnicos.nombre("Cliente E2E"),
                    DatosUnicos.correoCliente(),
                    "3001234567",
                    "Calle 10 # 20-30",
                    UUID.randomUUID().toString()
            );
        }
        return cliente;
    }

}
