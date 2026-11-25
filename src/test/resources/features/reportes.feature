# language: en
@reportes @regresion
Feature: Consulta de reportes del negocio
  Como administrador del negocio
  Quiero consultar el uso y la ocupación en períodos determinados
  Para evaluar el comportamiento de servicios y eventos

  @smoke @api @cuenta_prueba
  Scenario: Consulta de uso de servicios con reservas en el período
    Given que existen servicios con reservas registradas dentro de un período
    When consulta por API el uso de los servicios en el período
    Then debe visualizar cada servicio y su cantidad correcta de reservas

  @camino_alterno @cuenta_prueba
  Scenario: Consulta de uso de servicios sin datos en el período
    Given que el administrador inició sesión
    And que el administrador selecciona un período futuro sin datos de prueba
    When solicita el reporte de uso para ese período
    Then debe informarse que no existen datos de uso

  @smoke @api @cuenta_prueba
  Scenario: Consulta de ocupación de servicios con datos en el período
    Given que existen servicios con horarios y reservas dentro de un período
    When consulta por API la ocupación de los servicios en el período
    Then debe visualizar horarios totales, horarios reservados y porcentaje por servicio
    And ningún servicio debe tener más horarios reservados que horarios totales

  @camino_alterno @cuenta_prueba
  Scenario: Consulta de ocupación de servicios sin datos en el período
    Given que el administrador inició sesión
    And que el administrador selecciona un período futuro sin datos de prueba
    When solicita el reporte de ocupación para ese período
    Then debe informarse que no existen datos de ocupación

  @smoke @api @cuenta_prueba
  Scenario: Consulta de ocupación de eventos con reservas en el período
    Given que existen eventos con reservas confirmadas dentro de un período
    When solicita el reporte de ocupación de eventos para ese período
    Then debe visualizar los cupos reservados y el porcentaje de ocupación de cada evento

  @camino_excepcional @cuenta_prueba
  Scenario: La consulta de reportes rechaza un período invertido
    Given que el administrador inició sesión
    And que el administrador selecciona una fecha inicial posterior a la fecha final
    When solicita el reporte de ocupación de servicios
    Then debe mostrarse que la fecha inicial debe ser anterior a la fecha final

  @camino_feliz @limite @api @cuenta_prueba
  Scenario: El reporte de uso incluye el inicio y excluye el final del período
    Given que existen reservas confirmadas exactamente al inicio y al final de un período
    When consulta por API el uso de los servicios en el período
    Then el reporte cuenta la reserva del inicio y excluye la del final


