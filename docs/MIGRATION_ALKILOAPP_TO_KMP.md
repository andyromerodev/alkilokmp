# Migracion AlkiloApp -> alkilokmp (solo lectura del proyecto Android)

Este documento registra el avance de la migracion definida:
- fuente de referencia: `/Users/andy/AndroidStudioProjects/AlkiloApp` (read-only)
- destino de implementacion: `/Users/andy/AndroidStudioProjects/alkilokmp`

## Estado actual

### Slice 0 (Foundation) - IMPLEMENTADO
- Estructura base en `commonMain`:
  - `core/` (result, errores, dispatcher, logger, contratos de plataforma)
  - `domain/` (modelos, repositorios, use cases base)
  - `data/` (stores en memoria, mappers, repositorio auth in-memory y supabase)
  - `presentation/` (MVI de Auth)
- Contratos multiplataforma `expect/actual`:
  - `NetworkStatusProvider`
  - `PlatformConfigProvider`
  - `PlatformLogger`
- Config Gradle para foundation:
  - serialization plugin
  - coroutines core/test
  - supabase libs
  - `BuildConfig` en Android para `SUPABASE_URL` y `SUPABASE_ANON_KEY`

### Slice 1 (Auth) - IMPLEMENTADO (v1)
- `AuthRepository` en dominio
- `SupabaseAuthRepository` adaptado a `commonMain` (sin dependencias Android)
- `ConfigErrorAuthRepository` cuando faltan llaves/configuracion de Supabase (sin login simulado)
- Use cases auth:
  - login, register, logout, getCurrentUser, restoreSession, observeAuth, observeProfile
- UI compartida:
  - `AuthState`, `AuthIntent`, `AuthEffect`
  - `AuthPresenter`
  - `AuthScreen`
- `App.kt` conectado a la feature Auth

## Pendiente (siguiente iteracion)

### Slice 2
- Properties + Favorites

### Slice 3
- Bookings + host bookings/detail/create

### Slice 4
- Host edit property + upload de imagenes via `expect/actual`

## Nota tecnica
- En esta iteracion, session/theme usan stores en memoria para destrabar migracion del slice Auth.
- Siguiente paso recomendado: reemplazar stores en memoria por implementaciones persistentes por plataforma.
