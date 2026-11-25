# Automatización E2E — Plataforma de Reservas

Proyecto independiente de automatización para la presentación final. No modifica el frontend, el backend ni el proyecto BlazeDemo utilizado como referencia.

## Arquitectura

- **Scaffold:** plugin `io.github.jumosqu12.screenplayarchitecture`.
- **Patrón:** Screenplay (`Tasks`, `Questions`, `Targets`, actores y modelos).
- **Navegador:** Selenium WebDriver con Chrome.
- **BDD:** Cucumber/Gherkin con palabras clave en inglés y comportamiento descrito en español.
- **Reporte:** Serenity BDD con capturas por acción.
- **Aislamiento:** datos únicos y navegador reiniciado por escenario.

El inventario contiene **64 casos**. Gradle ejecuta **63 escenarios de QA** en Serenity. Los tres recorridos integrales usan Chrome para el registro y las operaciones de reserva, con datos preparados y comprobados mediante la API. Los casos `@api` verifican contratos y reglas directamente en QA. El caso `@reloj_fijo` se verifica en el backend con Maven para que pasen exactamente doce horas entre el reloj y el inicio del evento.

## Requisitos

1. JDK 17 configurado en `JAVA_HOME`. No ejecutar esta versión del proyecto con JDK 24.
2. Google Chrome instalado.
3. Cuentas activas de cliente y administrador para los escenarios con `@cuenta_prueba`. El runner completo crea registros de prueba.

## Credenciales seguras

Las claves no se guardan en Git ni deben enviarse por el chat. En PowerShell se configuran como variables de entorno para la sesión actual:

```powershell
$env:RESERVAS_CLIENTE_EMAIL="cliente-pruebas@ejemplo.com"
$env:RESERVAS_CLIENTE_PASSWORD="CLAVE"
$env:RESERVAS_ADMIN_EMAIL="admin-pruebas@ejemplo.com"
$env:RESERVAS_ADMIN_PASSWORD="CLAVE"

# Toda la automatización implementada, incluidas las pruebas con cuenta
.\gradlew.bat clean test
```

## Ejecuciones recomendadas

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

# Escenarios públicos que se pueden ejecutar sin cuenta
.\gradlew.bat clean test '-Dcucumber.filter.tags=@publico and not @pendiente'

# Navegación rápida para la presentación
.\gradlew.bat clean test -Prunner=SmokeRunner

# Tres recorridos integrales con Chrome
.\gradlew.bat clean test -Prunner=FlujosCriticosRunner

# Abrir el reporte después de ejecutar la suite
Invoke-Item .\target\site\serenity\index.html
```

El reporte queda en `target/site/serenity/index.html`.

Para ejecutar los 64 casos con un solo comando, configura antes las credenciales de QA y `JAVA_HOME` para Gradle. Pasa la ruta de un JDK compatible con el backend para la prueba de reloj fijo. El script usa el repositorio de backend vecino por defecto y acepta `-Backend` si está en otra ruta.

```powershell
.\scripts\validar-todo.ps1 -JavaBackend 'C:\Program Files\Java\jdk-24'
```

El resultado del caso de reloj fijo se guarda en Surefire dentro del backend. Serenity muestra las pruebas ejecutadas contra el entorno de QA.

## Validar las features sin abrir el navegador

La validación en seco comprueba la asociación de pasos para el runner seleccionado. Para revisar el inventario completo:

```powershell
.\scripts\validar-features.ps1
```

## Principio FIRST

- **Fast:** runners separados permiten ejecutar solo smoke o críticos.
- **Independent:** cada escenario crea datos únicos y reinicia el navegador.
- **Repeatable:** fechas y nombres se calculan en ejecución; las credenciales se inyectan.
- **Self-validating:** las `Questions` leen la UI y las aserciones deciden el resultado.
- **Timely:** los escenarios representan las historias y reglas antes de la demostración.

La API de QA se puede cambiar con `RESERVAS_API_URL`. Usa una base de pruebas: los escenarios crean cuentas, servicios, recursos, horarios, reservas y eventos con datos únicos.
