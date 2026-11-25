# language: en
@reservas @servicios @regresion @api @cuenta_prueba
Feature: Disponibilidad, creación y cancelación de reservas de servicios
  Como cliente
  Quiero consultar horarios y administrar mis reservas
  Para recibir un servicio en un horario válido

  @e2e @critico @ui
  Scenario: Configuración y reserva completa de un servicio con recurso
    Given que el administrador registra un recurso activo con disponibilidad futura
    And registra un servicio único que requiere ese recurso
    And registra un proveedor y lo asigna al servicio
    And el proveedor inicia sesión y publica un horario cubierto por la disponibilidad del recurso
    When un cliente activo inicia sesión y reserva el horario disponible
    Then la reserva debe quedar confirmada con el recurso asignado
    And el horario no debe continuar disponible para otros clientes
    And la reserva debe aparecer en los historiales del cliente, proveedor y negocio
    And los reportes de uso y ocupación deben incluir la nueva reserva con valores consistentes

  Rule: Gestión de reservas por el cliente

    Background:
      Given que un cliente activo inició sesión

    @smoke
    Scenario: Consulta de un servicio con horarios disponibles
      Given que existe un servicio activo con un horario futuro disponible
      When el cliente consulta la disponibilidad del servicio
      Then debe visualizar el horario disponible

    @camino_alterno
    Scenario: Consulta de un servicio sin horarios disponibles
      Given que existe un servicio activo sin horarios disponibles
      When el cliente consulta la disponibilidad del servicio
      Then debe informarse que no existen horarios disponibles

    Scenario: Reserva exitosa de un servicio sin recurso requerido
      Given que existe un servicio sin recurso y con un horario disponible
      When el cliente reserva el horario
      Then la reserva de servicio debe quedar confirmada
      And el horario debe dejar de estar disponible

    @smoke
    Scenario: Reserva exitosa de un servicio con recurso disponible
      Given que existe un servicio con un recurso activo y disponible durante un horario
      When el cliente reserva ese horario
      Then la reserva debe quedar confirmada con el recurso asociado
      And el horario debe dejar de estar disponible para otros clientes

    @camino_excepcional
    Scenario: Reserva rechazada porque el horario ya está ocupado
      Given que existe un horario con una reserva confirmada
      When el cliente intenta reservar el mismo horario
      Then la nueva reserva debe rechazarse por falta de disponibilidad
      And la reserva existente debe permanecer sin cambios

    @camino_excepcional
    Scenario: Reserva rechazada porque el recurso requerido no está disponible
      Given que existe un servicio cuyo recurso no cubre el período del horario
      When el cliente intenta reservar ese horario
      Then la reserva debe rechazarse por falta de disponibilidad del recurso
      And no debe crearse una nueva reserva

    Scenario: Cancelación exitosa con más de doce horas de anticipación
      Given que el cliente tiene una reserva confirmada que inicia en veinticuatro horas
      When cancela la reserva
      Then la reserva de servicio debe quedar cancelada
      And el horario y el recurso deben quedar nuevamente disponibles durante ese período

    @camino_excepcional
    Scenario: Cancelación rechazada con menos de doce horas de anticipación
      Given que el cliente tiene una reserva confirmada que inicia en seis horas
      When intenta cancelar la reserva
      Then la cancelación del servicio debe rechazarse por el plazo mínimo de doce horas
      And la reserva de servicio debe permanecer confirmada


