# CODEX_WORKING_CONTEXT

Última actualización: 2026-04-12 (Consistencia Slice 2 + roadmap actualizado)
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
6. **Repositorios no llaman SDKs remotos directamente** (Supabase/Ktor/etc.): usan `RemoteDataSourceContract`.
7. **Base de migración funcional**: usar como guía las implementaciones ya existentes en `/Users/andy/AndroidStudioProjects/AlkiloApp` (solo lectura), adaptándolas a KMP en `alkilokmp`.
8. **Uso obligatorio de skills KMP en cada tarea**: aplicar siempre los skills de KMP para mantener Clean Architecture + SOLID (mínimo `kmp-clean-architecture`; además `kmp-koin-di`, `kmp-mvi-compose`, `kmp-expect-actual`, `kmp-supabase-multiplatform` según corresponda).
9. **Trabajo UI/UX obligatorio con Impeccable**: para cambios de diseño/presentación usar skill `impeccable` y respetar la guía de `docs/IMPECCABLE_SKILLS.md`.

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

### 1.1) Slice 2 (parcialmente completado)
- Implementado núcleo funcional de:
  - `PropertyList` (datos de Supabase + toggle de favoritos)
  - `Favorites` (lista filtrada por favoritos)
- Añadidos:
  - modelos de dominio `Property`, `PropertyImage`, `PropertyType`
  - contratos de repositorio `PropertyRepositoryContract`, `FavoritesRepositoryContract`
  - contrato remoto `PropertyRemoteDataSourceContract`
  - use cases de propiedades/favoritos
  - repositorios `SupabasePropertyRepositoryImpl`, `FavoritesRepositoryImpl`
  - implementación remota `SupabasePropertyRemoteDataSourceImpl`
- Integración en `MainTabs` reemplazando placeholders de Playa/Favoritas.
- Paginación implementada en `PropertyList`:
  - consulta paginada por backend (`page`, `pageSize`, `range`)
  - soporte por tipo en backend (`PropertyType?` en query)
  - carga incremental al llegar al final de la lista (`LoadNextPage`)
- Estado actual del Slice 2:
  - **Completado**: `PropertyList` real, `Favorites` funcional base, `PropertyDetail` real, navegación a detalle y flujo de reserva desde detalle.
  - **Pendiente de cierre UX**: parity visual/estados de `Favorites` con `PropertyList/PropertyDetail` y consolidación de retries/snackbar.

### 1.2) Slice 2.1 (completado) — PropertyDetail real
- Implementado detalle de propiedad end-to-end en `commonMain`:
  - `PropertyRemoteDataSourceContract.getPropertyById(id)` + `SupabasePropertyRemoteDataSourceImpl`
  - `PropertyRepositoryContract.getPropertyById(id)` + `SupabasePropertyRepositoryImpl`
  - `GetPropertyByIdUseCase`
  - MVI completo: `PropertyDetailState/Intent/Effect/ViewModel` (heredando `BaseViewModel`)
  - `PropertyDetailScreen` funcional (loading/error/content, imágenes, descripción, amenities, CTA reservar)
- Navegación conectada en `NavGraph` con Koin parameters:
  - `PropertyDetailViewModel` se resuelve con `parametersOf(propertyId)`.
- Se conserva e integra el cambio local existente en `FavoritesViewModel` (page size de carga local ajustado por trabajo paralelo de Claude).

### 1.3) Slice 2.2 (completado) — UI Polish: shimmer, snackbar, ErrorState compartido

#### Componentes compartidos nuevos en `presentation/components/`
- **`ShimmerEffect.kt`**: `Modifier.shimmerEffect()` extraído como extensión pública compartida.  
  - Antes estaba duplicado como `internal` en `list/components/PropertyCardShimmer.kt`.
  - Ahora cualquier pantalla puede importarlo desde `presentation.components`.
- **`ErrorState.kt`**: composable movido desde `list/components/` a `presentation/components/` y hecho público.  
  - Parámetros: `message`, `onRetry`, `modifier`, `icon: DrawableResource?`.  
  - Usado tanto en `PropertyListScreen` como en `PropertyDetailScreen`.
- **`AppSnackbarHost.kt`**: host de snackbar personalizado con `SnackbarManager` wired en `NavGraph`.

#### PropertyDetailScreen — estado de carga
- `CircularProgressIndicator` reemplazado por **`PropertyDetailShimmer`** (skeleton completo).
- `PropertyDetailShimmer` en `presentation/property/detail/PropertyDetailShimmer.kt`:
  - `LazyColumn` que replica el layout exacto del contenido real: hero image, chip de tipo, título (2 líneas), dirección, rating/precio, descripción (3 líneas), amenities (LazyRow), stat chips, host card, mapa placeholder.
  - Usa `shimmerEffect()` compartido; recibe `contentPadding: PaddingValues` igual que `PropertyDetailContent`.
- Estado de error usa `ErrorState` compartido con retry vía `PropertyDetailIntent.Load`.

#### PropertyListScreen — refactor de componentes
- `SearchHeader` extraído a `list/components/SearchHeader.kt`.
- `ErrorState` extraído a `presentation/components/ErrorState.kt` (compartido).
- `PropertyCardShimmer` actualizado para importar `shimmerEffect` desde `presentation/components`.
- Nuevos intents/effects para búsqueda y filtros en `PropertyListIntent` y `PropertyListEffect`.
- `NavGraph` conecta `AppSnackbarHost` y `SnackbarManager`; `MainTabsScreen` pasa callbacks de snackbar.

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
5. En data layer, patrón obligatorio: `RepositoryImpl -> Remote/LocalDataSourceContract -> Impl concreta`.
6. Auditoría 2026-04-09: no quedan repositorios con acceso directo a Supabase/Ktor; `Auth` y `Property` ya pasan por `RemoteDataSourceContract`.
7. `PropertyDetail` ahora sigue el mismo patrón de Clean Architecture que `PropertyList`: acceso remoto solo vía `PropertyRemoteDataSourceContract`.

## Decisiones de UI/Presentación (2026-04-09)

### Patrón de Scaffold: NO anidar Scaffolds dentro de tabs
- `MainTabsScreen` es el **único Scaffold** para las tabs de cliente.
- `PropertyListScreen` y `FavoritesScreen` **no tienen Scaffold propio**.
- Ambas reciben `contentPadding: PaddingValues` desde el Scaffold padre.
- El `LazyColumn` aplica `contentPadding.calculateBottomPadding()` como bottom padding para que el contenido se scrollee por encima del nav flotante.
- Status bar top padding se obtiene con `WindowInsets.statusBars.asPaddingValues()` directamente.
- **Regla**: todo nuevo tab screen en `MainTabsScreen` o `HostTabsScreen` debe seguir este patrón — sin Scaffold propio, recibir `contentPadding`.

### BottomNav flotante (`AlkiloBottomBar`)
- Diseño: `Surface(shape = RoundedCornerShape(36.dp), color = surface.copy(alpha = 0.92f))` dentro de `Box(padding(horizontal=20.dp, vertical=12.dp))`.
- Transparencia parcial (`alpha = 0.92f`) para que los cards debajo sean visibles al hacer scroll.
- `FloatingNavItem`: ícono dentro de una píldora animada (`animateColorAsState` spring) — solo el ícono lleva fondo coloreado; el label queda abajo con color primario cuando está seleccionado.
- Items con `weight(1f)` para distribución uniforme.
- `BottomNavItem` tiene campo `icon: ImageVector` obligatorio — siempre proveer ícono al instanciar.

### Componentes compartidos en `presentation/components/`
- **`ShimmerEffect.kt`**: `Modifier.shimmerEffect()` — animación brush linear con `FastOutSlowInEasing`, 1500ms. Usar siempre este en lugar de re-implementar.
- **`ErrorState.kt`**: columna centrada con icono opcional, mensaje y botón "Reintentar". Usar en toda pantalla con estado de error que permita retry.
- **`AppSnackbarHost.kt`**: host + manager de snackbars globales. Wired en `NavGraph`; las screens reciben `onShowMessage: (String) -> Unit`.
- **Regla**: antes de crear un componente de estado (shimmer, error, vacío) en un feature, verificar si ya existe en `presentation/components/` y reutilizarlo.

### SearchHeader en PropertyListScreen
- Extraído como composable privado `SearchHeader(userName, query, onQueryChange, selectedType, onSelectType)`.
- Muestra greeting `"Hola, $userName 👋"` + fila de ubicación si `userName` no está vacío; de lo contrario muestra `"Explorar propiedades"`.
- `userName` viene de `ObserveCurrentProfileUseCase` → `profile.fullName.split(" ").first()` observado en `MainTabsScreen`.
- Barra de búsqueda: `TextField` + `RoundedCornerShape(50)` + `TextFieldDefaults.colors(indicatorColor = Transparent)` (pill, sin línea inferior).
- Scroll-hide: `AnimatedVisibility(slideInVertically + fadeIn / slideOutVertically + fadeOut)` controlado por `snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }` con umbral ±10px.
- Header siempre visible cuando `filteredProperties.size <= 3` o `isLoading`.

## Qué no romper en próximas tareas
1. No reintroducir código Android-only en `commonMain`.
2. No volver a `InMemory` para sesión/theme/favorites en Android/iOS/JVM.
3. No cambiar convenciones `Contract`/`Impl`.
4. No saltarse `BaseViewModel` para nuevos ViewModels de presentación compartida.
5. No hardcodear navegación en `App.kt`; enrutar desde `navigation/NavGraph.kt`.
6. No mover llamadas `postgrest/auth/storage` a repositorios; mantenerlas en `RemoteDataSourceImpl`.
7. No añadir `Scaffold` dentro de screens que viven como tabs en `MainTabsScreen` o `HostTabsScreen`; usar patrón `contentPadding: PaddingValues`.
8. No instanciar `BottomNavItem` sin `icon`; campo obligatorio desde 2026-04-09.
9. No reimplementar `shimmerEffect` ni `ErrorState` en features individuales; importar desde `presentation/components/`.
10. No usar `CircularProgressIndicator` como estado de carga en pantallas completas; usar skeleton shimmer que refleje el layout real.
11. No implementar UI nueva sin revisar `docs/IMPECCABLE_SKILLS.md` y sin aplicar lineamientos de `impeccable`.

## Pendientes inmediatos recomendados
1. Completar `Favorites` con el mismo nivel de polish de estados (`shimmer/retry/snackbar`) y layout final.
2. Crear `FavoritesShimmer` siguiendo el mismo patrón que `PropertyDetailShimmer`.
3. Conectar retry real en `PropertyListViewModel` y `FavoritesViewModel` cuando falla la carga inicial.
4. Migrar pantallas placeholder restantes de `HostTabs/AdminBookings/CreateBooking` con features reales.
5. Implementar almacenamiento real en JS/Wasm (localStorage).
6. Añadir mappers de `hostcache` <-> dominio cuando se migren slices 3 y 4.

## Próximas tareas (plan operativo)

### Prioridad alta (siguiente bloque)
1. **Slice 2: cierre de UX en cliente (pendiente final)**
   - Cerrar parity de `Favorites` con `PropertyList/PropertyDetail` en estados de carga/error/vacío.
   - Completar retry inicial y snackbars de error/éxito consistentes en cliente.
2. **Settings + Theme**
   - Migrar pantalla de settings y flujo de cambio de tema usando `ThemeRepositoryContract`.
   - Persistir elección de tema en store local ya implementado.
3. **Navegación real de tabs**
   - Mantener `Routes` como fuente única de verdad.
   - Validar navegación de tabs (main y host) sin perder estado de pantalla al cambiar tab.

### Prioridad media
1. **Slice 3: Bookings + Host**
   - Migrar `CreateBooking`, `HostProperties`, `HostBookings`, `HostBookingDetail`.
   - Integrar `hostcache` en repositorios para lectura/escritura de cache.
2. **Mapeos de hostcache**
   - Añadir mappers `domain <-> cache` para bookings/propiedades host.
   - Definir reglas de invalidez/refresh de cache.
3. **Auth UX de salida**
   - Implementar logout en settings con limpieza de stores de sesión y navegación a login.

### Prioridad baja (hardening)
1. **Web persistence real**
   - Reemplazar in-memory en JS/Wasm por `localStorage`.
2. **Recursos multiplataforma**
   - Consolidar strings y recursos visuales en `composeResources` para evitar hardcodes.
3. **Observabilidad**
   - Asegurar logging consistente por target para fallos de red/auth/supabase.

### Definición de terminado por tarea
1. Sin imports Android-only en `commonMain`.
2. Respeta naming `Contract`/`Impl` y herencia de `BaseViewModel`.
3. Navegación integrada en `NavGraph` (no callbacks sueltos en `App.kt`).
4. Update de este documento al cerrar cada bloque.

## Checklist para cada nueva tarea
1. Confirmar si es **solo alkilokmp**.
2. Revisar este archivo antes de editar.
3. Aplicar skill KMP correspondiente (obligatorio `kmp-clean-architecture`; sumar `kmp-koin-di`, `kmp-mvi-compose`, `kmp-expect-actual`, `kmp-supabase-multiplatform` según tarea).
4. Si hay UI/presentación, aplicar `impeccable` + validar contra `docs/IMPECCABLE_SKILLS.md`.
5. Mantener capas y nombres (`Contract`/`Impl`).
6. Si afecta auth/startup, validar flujo Splash -> role route.
7. Documentar al final cambios en este mismo archivo (append o actualización de secciones).
