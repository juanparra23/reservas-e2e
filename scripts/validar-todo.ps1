param(
    [string]$Backend = (Join-Path $PSScriptRoot '..\..\Fabrica-2026-2-EAP05'),
    [Parameter(Mandatory)]
    [string]$JavaBackend
)

$ErrorActionPreference = 'Stop'
$PSNativeCommandUseErrorActionPreference = $false
$proyecto = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$backendResuelto = (Resolve-Path $Backend).Path
$javaBackendResuelto = (Resolve-Path $JavaBackend).Path
$javaOriginal = $env:JAVA_HOME
$estadoQa = 0
$estadoReloj = 0

Push-Location $proyecto
try {
    & .\gradlew.bat clean test '-Dheadless.mode=true'
    $estadoQa = $LASTEXITCODE
} finally {
    Pop-Location
}

Push-Location $backendResuelto
try {
    $env:JAVA_HOME = $javaBackendResuelto
    & .\mvnw.cmd -B '-Dtest=EventoServiceTest#cancelarReservaEvento_exactamente12Horas_cancelaReserva' test
    $estadoReloj = $LASTEXITCODE
} finally {
    $env:JAVA_HOME = $javaOriginal
    Pop-Location
}

if ($estadoQa -ne 0 -or $estadoReloj -ne 0) {
    throw "Falló alguna comprobación. Serenity: $estadoQa. Prueba de reloj fijo: $estadoReloj."
}

Write-Host "Serenity: $(Join-Path $proyecto 'target\site\serenity\index.html')"
Write-Host "Reloj fijo: $(Join-Path $backendResuelto 'target\surefire-reports\Fabrica_EAP05.Reservas.Service.EventoServiceTest.txt')"
