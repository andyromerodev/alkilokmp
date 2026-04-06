---
name: kmp-koin-di
description: Configurar inyección de dependencias con Koin en proyectos Kotlin Multiplatform. Usar cuando se necesite wiring de casos de uso, repositorios y adaptadores de plataforma sin acoplar commonMain a implementaciones Android o iOS.
---

# KMP Koin DI

## Separar módulos
1. Módulo shared en `commonMain`:
   - Casos de uso.
   - Presenters/ViewModels compartidos.
   - Contratos de dominio.
2. Módulos por target:
   - Implementaciones de repos.
   - Providers de plataforma.
   - Configuración de engines/clientes nativos.

## Composición de módulos
1. Crear función `initKoin(platformModules)` en común.
2. Pasar módulos de plataforma desde cada host (Android/iOS/Desktop/Web).
3. Evitar `single` de objetos Android en `commonMain`.

## Reglas prácticas
1. Inyectar interfaces en shared, no implementaciones.
2. No leer `BuildConfig`/`Info.plist` directo en dominio.
3. Resolver configuración sensible vía provider inyectado.

## Verificación mínima
1. Testear inicialización de DI en `commonTest` con fakes.
2. Validar que cada target registre sus bindings obligatorios.

