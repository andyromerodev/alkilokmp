---
name: kmp-expect-actual
description: Diseñar contratos expect/actual en Kotlin Multiplatform para reemplazar dependencias Android-only. Usar cuando commonMain necesite capacidades de plataforma como reloj, logger, configuración, red, almacenamiento seguro, archivos o device info.
---

# KMP expect/actual

## Diseñar contrato en commonMain
1. Definir API mínima y estable (`expect class` o interfaz + factory).
2. Evitar exponer tipos nativos de Android/iOS en el contrato.
3. Priorizar tipos Kotlin estándar (`String`, `Long`, `Flow`, modelos propios).

## Implementar actual por target
1. `androidMain`: usar SDK Android.
2. `iosMain`: usar Foundation/Apple APIs.
3. `jvmMain`: usar JVM estándar.
4. `jsMain` y `wasmJsMain`: usar APIs web cuando aplique.

## Reglas de robustez
1. Capturar excepciones de plataforma y mapear a `AppError`.
2. Evitar `if (platform)` en `commonMain`.
3. Mantener la firma de `actual` idéntica a `expect`.

## Patrones recomendados
1. Capabilities pequeñas: `Clock`, `Logger`, `NetworkStatusProvider`, `PlatformConfigProvider`.
2. Factory única por target para instanciación.
3. No mezclar lógica de dominio con detalle de plataforma.

