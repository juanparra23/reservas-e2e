package co.com.eap05.certificacion.reservas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class FormularioAutenticacion {
    public static final Target TITULO_REGISTRO = Target.the("título de registro")
            .located(By.xpath("//h1[normalize-space()='Crea tu cuenta']"));
    public static final Target TITULO_LOGIN = Target.the("título de inicio de sesión")
            .located(By.xpath("//h1[normalize-space()='Bienvenido de vuelta']"));
    public static final Target ENLACE_REGISTRO = Target.the("enlace Regístrate como cliente")
            .located(By.linkText("Regístrate como cliente"));
    public static final Target ENLACE_LOGIN = Target.the("enlace Inicia sesión")
            .located(By.linkText("Inicia sesión"));
    public static final Target CERRAR_SESION = Target.the("control para cerrar sesión")
            .located(By.xpath("//*[self::button or self::a][normalize-space()='Cerrar sesión']"));
    public static final Target HISTORIAL_CLIENTE = Target.the("enlace al historial del cliente")
            .located(By.cssSelector("a[href='/historial/'], a[href='/historial']"));
    public static final Target AVISO_HISTORIAL_PRIVADO = Target.the("aviso de inicio de sesión en el historial")
            .located(By.xpath("//main//*[normalize-space(text())='Inicia sesión para continuar']"));
    public static final Target NOMBRE = Target.the("nombre completo").located(By.id("nombre"));
    public static final Target CORREO = Target.the("correo electrónico")
            .located(By.xpath("(//input[@type='email' or contains(@placeholder,'correo') or contains(@placeholder,'Correo')])[1]"));
    public static final Target TELEFONO = Target.the("teléfono")
            .located(By.cssSelector("input[type='tel'], input#telefono, input[name='telefono']"));
    public static final Target DIRECCION = Target.the("dirección").located(By.id("direccion"));
    public static final Target CLAVE = Target.the("contraseña").located(By.xpath("(//input[@type='password'])[1]"));
    public static final Target CREAR_CUENTA = Target.the("botón Crear cuenta")
            .located(By.xpath("//button[normalize-space()='Crear cuenta']"));
    public static final Target INICIAR_SESION = Target.the("botón Iniciar sesión")
            .located(By.xpath("//button[normalize-space()='Iniciar sesión']"));
    public static final Target AYUDA_CLAVE = Target.the("requisito de longitud de la contraseña")
            .located(By.xpath("//*[normalize-space(text())='Mínimo 6 caracteres.']"));
    public static final Target CORREO_DUPLICADO = Target.the("mensaje de correo registrado")
            .located(By.xpath("//*[contains(normalize-space(text()),'ya está registrado')]"));
    public static final Target CREDENCIALES_INVALIDAS = Target.the("mensaje de credenciales inválidas")
            .located(By.xpath("//*[contains(normalize-space(text()),'Credenciales inválidas')]"));

    public static Target campoDeRegistro(String campo) {
        return switch (campo) {
            case "nombre" -> NOMBRE;
            case "email" -> CORREO;
            case "contraseña" -> CLAVE;
            case "teléfono" -> TELEFONO;
            case "dirección" -> DIRECCION;
            default -> throw new IllegalArgumentException("Campo de registro desconocido: " + campo);
        };
    }

    private FormularioAutenticacion() {
    }
}
