$ErrorActionPreference = 'Stop'

$featureRoot = Join-Path $PSScriptRoot '..\src\test\resources\features'
$files = Get-ChildItem $featureRoot -Recurse -Filter '*.feature'
$definitions = (Select-String -Path $files.FullName -Pattern '^\s*Scenario( Outline)?:').Count
$outlines = (Select-String -Path $files.FullName -Pattern '^\s*Scenario Outline:').Count
$tableRows = (Select-String -Path $files.FullName -Pattern '^\s*\|').Count
$dataRows = $tableRows - $outlines
$executions = $definitions - $outlines + $dataRows

Write-Host "Archivos feature: $($files.Count)"
Write-Host "Definiciones: $definitions"
Write-Host "Ejecuciones esperadas: $executions"

if ($files.Count -ne 8 -or $executions -ne 64 -or ($files | Where-Object DirectoryName -ne (Resolve-Path $featureRoot).Path)) {
    throw 'El inventario debe contener 64 casos de Gherkin en 8 features directamente bajo features/.'
}

$spanishKeywords = Select-String -Path $files.FullName -Pattern '^\s*(Característica|Antecedentes|Escenario|Esquema del escenario|Ejemplos|Dado|Cuando|Entonces|Y|Pero)(:|\s)'
if ($spanishKeywords) {
    throw 'Se encontraron palabras clave de Gherkin en español. La convención del proyecto exige keywords en inglés.'
}
