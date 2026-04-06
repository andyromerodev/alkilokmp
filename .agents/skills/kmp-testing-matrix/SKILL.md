---
name: kmp-testing-matrix
description: Definir estrategia de pruebas en Kotlin Multiplatform por matriz de targets. Usar cuando se necesite validar lógica compartida en commonTest y decidir qué pruebas quedan en androidTest, iosTest, jvmTest, jsTest o wasmJsTest.
---

# KMP Testing Matrix

## Pirámide recomendada
1. Base en `commonTest`:
   - Casos de uso.
   - Reducers MVI.
   - Reglas de validación/mapeo.
2. Pruebas por target solo para:
   - Integraciones nativas.
   - Permisos.
   - APIs de plataforma.

## Regla de cobertura por feature
1. Cada nueva lógica en `commonMain` debe tener test en `commonTest`.
2. Cada adapter de plataforma debe tener al menos un smoke test por target principal.
3. Definir criterio de cierre: compila y ejecuta Android + iOS simulator + JVM.

## Diseño de test
1. Usar fakes para repos/interfaces compartidas.
2. Evitar depender de red real en unit tests.
3. Verificar estados y efectos en presentación, no detalles de implementación.

## Pipeline manual recomendado
1. `commonTest` en cada cambio de dominio/presentación.
2. Smoke build por target al cerrar slice.
3. Reportar incompatibilidades por target como parte del Definition of Done.

