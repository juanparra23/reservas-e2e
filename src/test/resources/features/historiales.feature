# language: en
@historiales @regresion @api @cuenta_prueba
Feature: Consulta de historiales por rol
  Como usuario autenticado
  Quiero consultar las reservas relacionadas con mi rol
  Para conocer su información y estado actual

  @smoke
  Scenario: El cliente consulta su historial de reservas de servicios
    Given que un cliente inició sesión y tiene reservas con diferentes estados
    When consulta su historial de cliente
    Then debe visualizar únicamente sus reservas de servicios
    And cada reserva debe mostrar su estado actual

  Scenario: El proveedor consulta el historial de sus servicios
    Given que un proveedor inició sesión y tiene reservas asociadas a sus horarios
    When consulta su historial de proveedor
    Then debe visualizar únicamente las reservas relacionadas con sus servicios
    And cada reserva debe mostrar su estado actual

  @smoke
  Scenario: El administrador consulta el historial global del negocio
    Given que el administrador tiene reservas de servicios y eventos para consultar
    When consulta el historial del negocio
    Then debe visualizar las reservas de servicios y eventos
    And cada registro debe mostrar su tipo y estado actual


