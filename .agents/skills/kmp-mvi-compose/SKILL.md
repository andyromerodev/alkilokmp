---
name: kmp-mvi-compose
description: Implementar MVI compartido en Compose Multiplatform. Usar cuando una pantalla deba compartir estado y lógica entre Android, iOS, Desktop, JS y Wasm, con intents, reducer, effects y side-effects aislados detrás de interfaces.
---

# KMP MVI Compose

## Modelar contrato de pantalla en commonMain
1. Definir `UiState` como `data class`.
2. Definir `Intent` como `sealed interface`.
3. Definir `Effect` para eventos one-shot (navegación, toast/snackbar).

## Implementar reducer puro
1. Crear función que reciba estado + intent y retorne nuevo estado.
2. Evitar IO dentro del reducer.
3. Cubrir reducer con tests de `commonTest`.

## Manejar side-effects
1. Ejecutar use cases en presenter/viewmodel compartido.
2. Emitir `Effect` para navegación y acciones de host.
3. Mantener servicios de plataforma detrás de contratos.

## Integrar con Compose Multiplatform
1. Colectar estado con `StateFlow`.
2. Renderizar UI desde `UiState`.
3. Traducir interacciones UI a `Intent`.

## Errores y loading
1. Representar loading explícito en `UiState`.
2. Mapear errores técnicos a mensajes de dominio.
3. No propagar excepciones crudas a la UI.

