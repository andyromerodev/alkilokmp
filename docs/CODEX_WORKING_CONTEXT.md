# CODEX_WORKING_CONTEXT

Última actualización: 2026-04-07
Proyecto destino: `/Users/andy/AndroidStudioProjects/alkilokmp`
Proyecto fuente (solo lectura): `/Users/andy/AndroidStudioProjects/AlkiloApp`

## Objetivo de este documento
Mantener un historial operativo y reglas obligatorias para que cada nueva tarea continúe sobre las mismas decisiones arquitectónicas, sin romper convenciones ya acordadas.

## Reglas no negociables
1. **No tocar `AlkiloApp`**: se usa solo como referencia/copia adaptada.
2. **Todo cambio se hace en `alkilokmp`**.
3. **Builds/tests los ejecuta Andy manualmente** (Codex no debe correrlos salvo que se pida explícitamente).
4. **Nada simulado para login/auth**: flujo real con Supabase.
5. **Arquitectura limpia + SOLID** en KMP.

## Convenciones de arquitectura y nombres
1. Capas en `commonMain`: `core`, `domain`, `data`, `presentation`, `navigation`, `di`.
2. Contratos llevan sufijo `Contract`.
3. Implementaciones llevan sufijo `Impl`.
4. Una implementación concreta de contrato mantiene nombre explícito de tecnología (ej: `SupabaseAuthRemoteDataSourceImpl`).
5. `ViewModel` compartidos deben heredar de `BaseViewModel`.
6. `BaseViewModel` en `commonMain` hereda de `androidx.lifecycle.ViewModel` (multiplatform lifecycle).

## Estado implementado (resumen real)

### 1) Auth (Slice 1)
- Estructura separada:
  - `presentation/auth/login/*`
  - `presentation/auth/register/*`
  - `presentation/auth/shared/AuthValidators.kt`
- ViewModels:
  - `LoginViewModel`
  - `RegisterViewModel`
- Ambos heredan de `presentation/base/BaseViewModel.kt`.
- Login real contra Supabase vía repositorio.

### 2) Navegación base KMP
- Creada en `commonMain/navigation`:
  - `Routes.kt`
  - `NavGraph.kt`
- `App.kt` usa `AlkiloNavGraph`.
- Flujo post-login por rol:
  - CLIENT -> `MainTabs`
  - HOST -> `HostTabs`
  - ADMIN -> `AdminBookings`
- Soporte `returnPropertyId` para ir a `CreateBooking` cuando aplica.

### 3) Restauración de sesión al inicio
- Arranque en `Routes.Splash`.
- En splash se ejecuta restauración + observación de auth/profile:
  - `RestoreSessionUseCase`
  - `ObserveAuthStateUseCase`
  - `ObserveCurrentProfileUseCase`
- Si hay sesión válida, evita volver a Login.

### 4) DI con Koin (separada por módulos)
En `commonMain/di`:
- `AppModule.kt`
- `CoreModule.kt`
- `SupabaseModule.kt`
- `DataModule.kt`
- `DomainModule.kt`
- `PresentationModule.kt`
- `KoinInit.kt`

## Capa local implementada (basada en estructura de AlkiloApp)

### Estructura
- `data/local/session/*`
- `data/local/favorites/*`
- `data/local/settings/*`
- `data/local/hostcache/*`

### Persistencia multiplataforma
- Contrato + factory:
  - `core/platform/storage/KeyValueStoreContract.kt`
  - `expect class KeyValueStoreFactory`
- `actual` por target:
  - Android: SharedPreferences
  - iOS: NSUserDefaults
  - JVM: Preferences
  - JS/Wasm: in-memory temporal (pendiente llevar a localStorage real)

## Decisiones importantes ya tomadas
1. `internal` en varias clases de `commonMain` para evitar problemas de export ObjC/Kotlin Native.
2. Config Supabase viene de `local.properties`/env, generado a código (`GeneratedSupabaseConfig`).
3. Se eliminó el ejemplo de `Clock` que era solo didáctico.
4. Se eliminó el flujo auth antiguo (`AuthViewModel/AuthScreen`) y wrappers Android/iOS viejos.

## Qué no romper en próximas tareas
1. No reintroducir código Android-only en `commonMain`.
2. No volver a `InMemory` para sesión/theme/favorites en Android/iOS/JVM.
3. No cambiar convenciones `Contract`/`Impl`.
4. No saltarse `BaseViewModel` para nuevos ViewModels de presentación compartida.
5. No hardcodear navegación en `App.kt`; enrutar desde `navigation/NavGraph.kt`.

## Pendientes inmediatos recomendados
1. Reemplazar pantallas placeholder de `MainTabs/HostTabs/AdminBookings/CreateBooking` con features reales migradas.
2. Migrar `Favorites`, `Settings`, `Properties`, `Bookings` por slices.
3. Implementar almacenamiento real en JS/Wasm (localStorage).
4. Añadir mappers de `hostcache` <-> dominio cuando se migren slices 2 y 3.

## Checklist para cada nueva tarea
1. Confirmar si es **solo alkilokmp**.
2. Revisar este archivo antes de editar.
3. Mantener capas y nombres (`Contract`/`Impl`).
4. Si afecta auth/startup, validar flujo Splash -> role route.
5. Documentar al final cambios en este mismo archivo (append o actualización de secciones).

