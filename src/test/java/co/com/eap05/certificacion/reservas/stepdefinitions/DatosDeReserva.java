package co.com.eap05.certificacion.reservas.stepdefinitions;

import co.com.eap05.certificacion.reservas.api.ApiReservas;
import co.com.eap05.certificacion.reservas.model.Cliente;
import co.com.eap05.certificacion.reservas.model.ContextoEscenario;
import co.com.eap05.certificacion.reservas.utils.DatosUnicos;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

final class DatosDeReserva {
    final UsuariosDePrueba usuarios = new UsuariosDePrueba();
    static final String DESCRIPCION = "Datos desechables de automatización de QA";
    private ApiReservas proveedor;
    private Cliente credencialesProveedor;
    String proveedorId;
    String servicioId;
    String servicioNombre;
    String recursoId;
    String horarioId;
    String reservaId;
    LocalDateTime inicio;
    LocalDateTime fin;
    ApiReservas.Respuesta confirmacion;

    ApiReservas proveedor() {
        if (proveedor == null) {
            crearProveedor();
            proveedor = ApiReservas.iniciarSesion(credencialesProveedor.correo(), credencialesProveedor.clave());
        }
        return proveedor;
    }

    void crearProveedor() {
        if (proveedorId == null) {
            credencialesProveedor = new ContextoEscenario().clienteNuevo();
            proveedorId = usuarios.admin().post("/api/usuario/registrar/proveedor",
                    UsuariosDePrueba.datosDeRegistro(credencialesProveedor)).exige(201).id();
        }
    }

    String crearServicio() {
        servicioNombre = DatosUnicos.nombre("Servicio QA");
        servicioId = servicio(servicioNombre, recursoId).exige(201).id();
        return servicioId;
    }

    ApiReservas.Respuesta servicio(String nombre, String recurso) {
        Map<String, Object> datos = new HashMap<>(Map.of("nombre", nombre,
                "descripcion", DESCRIPCION, "duracionMin", 30));
        if (recurso != null) {
            datos.put("recursoId", recurso);
        }
        return usuarios.admin().post("/api/servicios/registrar", datos);
    }

    String crearRecurso() {
        recursoId = recurso(DatosUnicos.nombre("Recurso QA"), DESCRIPCION).exige(201).id();
        return recursoId;
    }

    ApiReservas.Respuesta recurso(String nombre, String descripcion) {
        return usuarios.admin().post("/api/recursos/registrar", Map.of("nombre", nombre, "descripcion", descripcion));
    }

    ApiReservas.Respuesta cambiarEstado(boolean activo) {
        return usuarios.admin().patch("/api/recursos/" + recursoId + "/estado", Map.of("activo", activo));
    }

    ApiReservas.Respuesta asignarProveedor() {
        crearProveedor();
        return usuarios.admin().post("/api/proveedor-servicio/asignar", Map.of(
                "servicioId", servicioId, "proveedorId", proveedorId));
    }

    void asignarPeriodo(int horas) {
        inicio = LocalDateTime.now(ZoneOffset.UTC).plusHours(horas).withNano(0);
        fin = inicio.plusMinutes(30);
    }

    ApiReservas.Respuesta definirDisponibilidad() {
        return usuarios.admin().post("/api/recursos/" + recursoId + "/disponibilidad", Map.of(
                "inicio", inicio.atOffset(ZoneOffset.UTC).toString(), "fin", fin.atOffset(ZoneOffset.UTC).toString()));
    }

    ApiReservas.Respuesta publicarHorario(LocalDateTime desde, LocalDateTime hasta) {
        return proveedor().post("/api/horarios", Map.of("proveedorId", proveedorId,
                "inicio", desde.toString(), "fin", hasta.toString()));
    }

    void prepararServicioConProveedor() {
        crearServicio();
        asignarProveedor().exige(201);
    }

    void prepararHorario(int horas, boolean conRecurso, boolean recursoDisponible) {
        asignarPeriodo(horas);
        if (conRecurso) {
            crearRecurso();
            if (recursoDisponible) {
                definirDisponibilidad().exige(201);
            }
        }
        prepararServicioConProveedor();
        horarioId = publicarHorario(inicio, fin).exige(201).id();
    }

    ApiReservas.Respuesta disponibilidad() {
        return usuarios.cliente().get("/api/servicios/" + servicioId + "/disponibilidad").exige(200);
    }

    ApiReservas.Respuesta reservar(ApiReservas usuario) {
        return usuario.post("/api/reservas/reservar", Map.of("servicioId", servicioId, "horarioId", horarioId));
    }

    void confirmarReserva(ApiReservas usuario) {
        confirmacion = reservar(usuario).exige(201);
        reservaId = confirmacion.id();
    }

    ApiReservas.Respuesta cancelar(ApiReservas usuario) {
        return usuario.post("/api/reservas/" + reservaId + "/cancelar", null);
    }
}
