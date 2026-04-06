---
name: kmp-clean-architecture
description: Implementar y refactorizar arquitectura limpia en Kotlin Multiplatform con Compose Multiplatform. Usar cuando se necesite organizar features en capas domain/data/presentation y platform, mover lógica a commonMain, aplicar SOLID sin acoplarse a Android, definir contratos compartidos y mantener adaptadores por target (androidMain, iosMain, jvmMain, jsMain, wasmJsMain).
---

# KMP Clean Architecture

## Definir límites por capa
1. Colocar en `commonMain`:
   - Entidades de dominio.
   - Casos de uso.
   - Interfaces de repositorio.
   - Estado/UI models puros.
2. Colocar en source sets de plataforma:
   - SDKs nativos.
   - Persistencia nativa.
   - Permisos, conectividad, filesystem, secure storage.

## Aplicar regla de dependencias
1. `presentation -> domain`.
2. `data -> domain`.
3. `domain` no depende de `data` ni de APIs de plataforma.
4. Implementaciones concretas en `data` o `platform`, nunca en `domain`.

## Migrar código Android a KMP
1. Copiar primero modelos y lógica pura a `commonMain`.
2. Reemplazar imports Android por interfaces en común.
3. Agregar adaptadores `actual` o implementaciones por target.
4. Validar que `commonMain` compile sin `android.*`.

## Checklist SOLID en KMP
1. SRP: cada caso de uso con una responsabilidad.
2. OCP: agregar target nuevo sin tocar dominio.
3. LSP: implementaciones de repos cumplen contrato compartido.
4. ISP: interfaces pequeñas por capability (`Clock`, `Logger`, `NetworkStatus`).
5. DIP: presentation/domain dependen de abstracciones.

## Convención recomendada de paquetes
1. `core`: tipos transversales (`Result`, `AppError`, utilidades).
2. `domain`: entidades, contratos, use cases.
3. `data`: repos impl, DTOs, mappers.
4. `presentation`: estado, intents, effects, presenters/viewmodels.
5. `platform`: adapters específicos por target.

