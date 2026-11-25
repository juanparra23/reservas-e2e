# language: en
@agenda @horarios @regresion @api @cuenta_prueba
Feature: Gestión de horarios y agenda del proveedor
  Como proveedor
  Quiero publicar, consultar y bloquear mis horarios
  Para controlar cuándo puedo recibir reservas

  Background:
    Given que un proveedor activo inició sesión

  @smoke
  Scenario: Registro exitoso de un horario disponible
    Given que el proveedor está asignado a un servicio
    And no tiene horarios que se crucen con el período seleccionado
    When publica un horario con inicio anterior al fin
    Then el horario debe quedar registrado como disponible
    And el horario debe conservar exactamente sus fechas de inicio y fin

  @camino_excepcional
  Scenario: El registro rechaza un horario solapado
    Given que el proveedor tiene un horario previamente registrado
    When intenta publicar otro horario que se cruza con el existente
    Then el nuevo horario no debe registrarse
    And debe mostrarse que existe un solapamiento

  Scenario: Consulta de agenda con reservas programadas
    Given que el proveedor tiene una reserva confirmada asociada a uno de sus horarios
    When consulta su agenda
    Then debe visualizar la reserva confirmada
    And debe visualizar la fecha y hora correspondientes

  @camino_alterno
  Scenario: Consulta de agenda sin reservas programadas
    Given que el proveedor no tiene reservas asociadas
    When consulta su agenda
    Then debe informarse que no existen reservas programadas

  @smoke
  Scenario: Bloqueo exitoso de un horario sin reserva
    Given que el proveedor tiene un horario disponible sin reservas confirmadas
    When bloquea el horario
    Then el horario debe quedar en estado bloqueado
    And no debe aparecer como disponible para los clientes

  @camino_excepcional
  Scenario: No se puede bloquear un horario con una reserva confirmada
    Given que el proveedor tiene un horario asociado a una reserva confirmada
    When intenta bloquear el horario
    Then la operación debe rechazarse porque existe una reserva
    And el horario y la reserva deben permanecer sin cambios

  @camino_alterno
  Scenario: Un horario contiguo puede publicarse sin considerarse solapado
    Given que el proveedor tiene un horario que finaliza a las diez de la mañana
    When publica otro horario que inicia exactamente a las diez de la mañana
    Then el nuevo horario debe quedar registrado como disponible
    And ambos horarios deben conservar sus períodos independientes


