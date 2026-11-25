# language: en
@servicios @regresion
Feature: Administración de servicios y asignación de proveedores
  Como administrador del negocio
  Quiero configurar los servicios y sus proveedores
  Para ofrecerlos posteriormente a los clientes

  @smoke @cuenta_prueba
  Scenario: Registro exitoso de un servicio sin recurso requerido
    Given que el administrador inició sesión
    And que el administrador dispone de datos únicos y válidos para un servicio
    When registra el servicio sin asociar un recurso
    Then el catálogo debe confirmar el registro del servicio
    And debe listar el servicio con el nombre proporcionado y un identificador

  @api @cuenta_prueba
  Scenario: Registro exitoso de un servicio con recurso requerido
    Given que existe un recurso activo
    When el administrador registra un servicio que requiere ese recurso
    Then el servicio debe quedar registrado
    And debe conservar la asociación con el recurso seleccionado

  @camino_excepcional @api @cuenta_prueba
  Scenario: El registro de servicio rechaza el nombre vacío
    Given que el administrador completa los demás datos válidos del servicio
    When intenta registrar el servicio sin nombre
    Then el servicio no debe crearse
    And debe mostrarse que el nombre es obligatorio

  @camino_excepcional @api @cuenta_prueba
  Scenario: El registro de servicio rechaza un recurso inexistente
    Given que el administrador utiliza un identificador de recurso inexistente
    When intenta registrar un servicio que requiere ese recurso
    Then el servicio no debe crearse
    And debe mostrarse que el recurso debe existir y estar activo

  @smoke @api @cuenta_prueba
  Scenario: Asignación exitosa de un proveedor a un servicio
    Given que existen un proveedor activo y un servicio registrado sin asociación entre ellos
    When el administrador asigna el proveedor al servicio
    Then debe existir una sola asociación entre el proveedor y el servicio

  @api @cuenta_prueba
  Scenario: Actualización exitosa de un servicio sin reservas asociadas
    Given que existe un servicio sin reservas asociadas
    When el administrador modifica su nombre, descripción y duración con valores válidos
    Then los datos del servicio deben quedar actualizados
    And el identificador del servicio debe permanecer igual

  @camino_alterno @api @cuenta_prueba
  Scenario: La asignación repetida no duplica la asociación entre proveedor y servicio
    Given que un proveedor ya está asignado a un servicio
    When el administrador intenta asignar nuevamente el mismo proveedor al servicio
    Then no debe crearse una asociación duplicada
    And debe permanecer una sola asociación entre ambos


