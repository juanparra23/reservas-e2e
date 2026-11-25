# language: en
@recursos @regresion
Feature: Administración y disponibilidad de recursos
  Como administrador del negocio
  Quiero registrar recursos y establecer cuándo pueden utilizarse
  Para asignarlos de manera segura a las reservas

  @smoke @cuenta_prueba
  Scenario: Registro exitoso de un recurso
    Given que el administrador inició sesión
    And que el administrador dispone de un nombre único y una descripción válida
    When registra el recurso
    Then el catálogo debe confirmar el registro del recurso
    And debe listar el recurso con el nombre proporcionado y un identificador

  @camino_excepcional @api @cuenta_prueba
  Scenario Outline: El registro de recurso rechaza un campo obligatorio vacío
    Given que el administrador completa los demás datos válidos del recurso
    When intenta registrar el recurso sin proporcionar "<campo>"
    Then el recurso no debe crearse
    And debe mostrarse que "<campo>" es obligatorio

    Examples:
      | campo      |
      | nombre     |
      | descripción|

  @smoke @api @cuenta_prueba
  Scenario: Registro exitoso de disponibilidad para un recurso activo
    Given que existe un recurso activo sin disponibilidad en el período seleccionado
    When el administrador define un inicio anterior al fin
    Then el período de disponibilidad debe quedar registrado
    And debe conservar exactamente sus fechas de inicio y fin

  @camino_excepcional @api @cuenta_prueba
  Scenario: No se puede desactivar un recurso con una reserva confirmada vigente
    Given que existe un recurso activo asociado a una reserva confirmada que todavía no finaliza
    When el administrador intenta desactivar el recurso
    Then la operación debe rechazarse porque el recurso está comprometido
    And el recurso debe permanecer activo

  @camino_feliz @api @cuenta_prueba
  Scenario: Desactivación exitosa de un recurso sin reservas vigentes
    Given que existe un recurso activo sin reservas confirmadas vigentes
    When el administrador desactiva el recurso
    Then el recurso debe quedar inactivo
    And no debe aparecer como disponible para nuevas reservas


