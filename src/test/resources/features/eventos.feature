# language: en
@eventos @regresion @cuenta_prueba @api
Feature: Creación, reserva, cancelación y capacidad de eventos
  Como usuario de la plataforma
  Quiero administrar correctamente los cupos de los eventos
  Para evitar sobrecupos y mantener su ocupación actualizada

  @smoke
  Scenario: Registro exitoso de un evento
    Given que el administrador de QA está autenticado en la API
    And dispone de un nombre único, descripción, inicio, fin y capacidad válidos
    When registra el evento
    Then el evento debe quedar registrado y activo
    And debe conservar los datos proporcionados

  @smoke
  Scenario: Reserva exitosa de un cupo
    Given que un cliente inició sesión
    And existe un evento activo con cupos disponibles
    When reserva un cupo
    Then la reserva debe quedar confirmada
    And los cupos disponibles deben disminuir en uno

  @camino_excepcional
  Scenario: Reserva rechazada en un evento sin cupos
    Given que un cliente inició sesión
    And existe un evento que alcanzó su capacidad máxima
    When intenta reservar un cupo
    Then la reserva debe rechazarse por falta de cupos
    And la ocupación del evento debe permanecer sin cambios

  Scenario: Cancelación exitosa con más de doce horas de anticipación
    Given que un cliente tiene una reserva confirmada para un evento que inicia en veinticuatro horas
    When cancela la reserva del evento
    Then la reserva debe quedar cancelada
    And los cupos disponibles deben aumentar en uno

  @camino_excepcional
  Scenario: Cancelación rechazada con menos de doce horas de anticipación
    Given que un cliente tiene una reserva confirmada para un evento que inicia en seis horas
    When intenta cancelar la reserva del evento
    Then debe mostrarse que no cumple la anticipación mínima de doce horas
    And la reserva debe permanecer confirmada

  @camino_excepcional
  Scenario: La capacidad cero es rechazada
    Given que el administrador inició sesión y existe un evento registrado
    When intenta establecer la capacidad del evento en cero
    Then la actualización debe rechazarse
    And debe mostrarse que la capacidad debe ser mayor que cero

  @camino_excepcional
  Scenario: La capacidad no puede quedar por debajo de las reservas confirmadas
    Given que el administrador de QA está autenticado en la API
    And existe un evento con dos reservas confirmadas
    When intenta establecer la capacidad en uno
    Then la actualización debe rechazarse
    And la capacidad y las reservas deben permanecer sin cambios

  # Ejecutado por EventoServiceTest con el reloj fijo del backend.
  @camino_feliz @limite @reloj_fijo
  Scenario: La cancelación se admite exactamente en el límite de doce horas
    Given que el reloj de prueba está fijo y el evento de la reserva comienza en doce horas
    When el cliente cancela la reserva del evento
    Then la reserva del evento debe quedar cancelada
    And el cupo debe quedar disponible

  @e2e @critico @ui
  Scenario: Reserva y cancelación anticipada de un evento libera nuevamente el cupo
    Given que el administrador registra un evento único que inicia en cuarenta y ocho horas con capacidad de dos cupos
    And un cliente activo reserva un cupo del evento desde el navegador
    When el cliente cancela la reserva del evento desde el navegador
    Then la reserva debe constar como cancelada en el historial del negocio
    And el evento debe volver a tener dos cupos disponibles
    And la reserva cancelada debe aparecer en el historial del negocio
    And el reporte del evento debe mostrar cero reservas activas y cero por ciento de ocupación


