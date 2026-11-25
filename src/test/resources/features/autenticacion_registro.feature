# language: en
@autenticacion @regresion
Feature: Registro y autenticación de usuarios
  Como usuario de la plataforma
  Quiero registrar una cuenta y autenticarme de forma segura
  Para acceder únicamente a las funciones permitidas para mi rol

  @cuenta_prueba
  Scenario: Registro exitoso de un cliente nuevo
    Given que una persona utiliza datos personales válidos y un correo único
    When registra una cuenta de cliente
    Then puede iniciar sesión con la cuenta creada
    And debe visualizar las opciones correspondientes a su rol

  @publico @camino_excepcional
  Scenario Outline: El formulario de registro exige cada campo obligatorio
    Given que el visitante abre el registro de clientes
    When deja vacío el campo de registro "<campo>"
    Then el navegador debe invalidar el campo de registro "<campo>" por estar vacío

    Examples:
      | campo      |
      | nombre     |
      | email      |
      | contraseña |
      | teléfono   |
      | dirección  |

  @publico @camino_excepcional
  Scenario: El registro rechaza una contraseña menor a seis caracteres
    Given que el visitante abre el registro de clientes
    When escribe una contraseña de cinco caracteres en el registro
    Then el formulario debe indicar que la contraseña requiere mínimo seis caracteres
    And el navegador debe invalidar la contraseña por su longitud

  @cuenta_prueba @camino_excepcional
  Scenario: El registro rechaza un correo previamente registrado
    Given que existe una cuenta asociada a un correo
    When otra persona intenta registrarse utilizando el mismo correo
    Then debe mostrarse que el correo ya está registrado
    And el visitante debe permanecer en el formulario de registro

  @cuenta_prueba @smoke
  Scenario: Inicio de sesión exitoso
    Given que existe un usuario activo con credenciales válidas
    When inicia sesión con su correo y contraseña
    Then debe acceder a la plataforma
    And debe visualizar las opciones correspondientes a su rol

  @cuenta_prueba @camino_excepcional
  Scenario: Inicio de sesión rechazado por contraseña incorrecta
    Given que existe un usuario activo
    When intenta iniciar sesión con una contraseña incorrecta
    Then debe mostrarse un mensaje de credenciales inválidas
    And debe ver el formulario de inicio de sesión

  @cuenta_prueba @smoke
  Scenario: Cierre de sesión exitoso
    Given que un usuario tiene una sesión activa
    When cierra su sesión
    Then debe regresar a la pantalla de inicio de sesión
    And no debe acceder a una página protegida con la sesión anterior

  @publico @camino_excepcional @seguridad
  Scenario: Un visitante debe iniciar sesión para ver su historial
    Given que una persona no tiene una sesión activa
    When intenta abrir directamente su historial
    Then debe ver que necesita iniciar sesión para consultar sus reservas

  @cuenta_prueba @api @camino_excepcional @seguridad
  Scenario: Un cliente no puede registrar proveedores
    Given que un cliente de QA está autenticado en la API
    When solicita registrar un proveedor con datos válidos
    Then la API rechaza el registro de proveedor con HTTP 403

  @publico @smoke
  Scenario: El visitante abre el registro desde el inicio de sesión
    Given que el visitante abre el inicio de sesión
    When elige registrarse como cliente
    Then debe ver el formulario de registro del cliente

  @publico @smoke
  Scenario: El visitante vuelve al inicio de sesión desde el registro
    Given que el visitante abre el registro de clientes
    When elige iniciar sesión desde el registro
    Then debe ver el formulario de inicio de sesión

  @e2e @critico @onboarding @ui @cuenta_prueba
  Scenario: Un cliente nuevo se registra, inicia sesión y reserva su primera cita
    Given que el administrador y el proveedor prepararon un servicio único con un horario disponible
    When una persona se registra como cliente utilizando datos válidos y un correo único
    And inicia sesión con las credenciales recién registradas
    And consulta el servicio y reserva el horario disponible
    Then la primera reserva queda confirmada en el sistema
    And el horario debe dejar de estar disponible para otros clientes
    And la reserva debe aparecer confirmada en el historial del nuevo cliente


