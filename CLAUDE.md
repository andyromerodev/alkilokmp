# Alkilo KMP - Project Guidelines

## Design Context

### Users
**Primary audience**: Cubanos locales buscando alquilar propiedades dentro de Cuba.

**Context of use**:
- Conexión de datos móviles limitada — la app debe cargar rápido y consumir pocos datos
- Usuarios buscan alojamiento para viajes internos, reuniones familiares, escapadas
- Mezcla de edades y niveles técnicos — interfaz intuitiva y clara

### Brand Personality
**Three words**: Confiable, profesional, accesible

- Serio pero no frío — transmite confianza
- Directo y claro — respeta el tiempo del usuario
- Sin florituras — cada elemento tiene propósito

### Aesthetic Direction
**Visual tone**: Minimalismo cálido

- **Limpio y despejado** — Espacios generosos, sin ruido visual
- **Neutros cálidos** — Arena, beige, terracota suave, blancos cremosos
- **Fotografía como hero** — Las propiedades son el protagonista

**Theme**: Light mode default, dark mode como alternativa

### Design Principles

1. **Performance es UX** — Cada KB cuenta. Optimizar para datos limitados.
2. **Confianza visual** — Consistencia genera seguridad.
3. **Fotografía primero** — La UI se aparta para que las imágenes brillen.
4. **Jerarquía implacable** — Una acción principal por pantalla.
5. **Accesibilidad práctica** — Contraste alto, touch targets generosos.

### Technical Stack
- Kotlin Multiplatform (Android, iOS, Web, Desktop)
- Compose Multiplatform + Material3
- Clean Architecture + MVI + Koin DI
- Supabase backend

---

## Session Tools (always active)

### context-mode v1.0.89
**Propósito**: Procesa comandos que generan mucho output en sandbox, dejando solo resúmenes indexados en FTS5. Ahorra 60–90% tokens automáticamente.

**Uso automático** (via hooks):
- Intercepta comandos Bash que generan >20 líneas
- Indexa automáticamente los resultados en base de datos FTS5 local
- Mantiene el contexto limpio — solo resúmenes entran

**Uso manual** (cuando investigas):
```
mcp__plugin_context-mode_context-mode__ctx_batch_execute(
  commands: [{label: "Feature list", command: "ls -la"}, ...],
  queries: ["search term 1", "search term 2", ...]
)
```
→ Ejecuta comandos + busca en output en UNA llamada (60% más eficiente)

```
mcp__plugin_context-mode_context-mode__ctx_search(
  queries: ["architecture decision", "auth bug"],
  source: "session-events"
)
```
→ Busca en índice FTS5 sin re-ejecutar comandos

**Cuándo usar qué**:
- Bash: solo git/mkdir/rm/mv, navegación simple
- `ctx_batch_execute`: investigación inicial (build logs, git history, grep múltiple)
- `ctx_execute`/`ctx_execute_file`: análisis de datos, API calls, procesamiento

### claude-mem
**Propósito**: Memoria persistente entre sesiones. Guarda decisiones arquitectónicas, patrones, bugs, y contexto del proyecto para NO repetir trabajo.

**Tipos de memoria guardados automáticamente**:
- `user` — tu rol, preferencias, experiencia
- `feedback` — correcciones tuyas ("no hagas X porque Y"), validaciones ("sí, ese approach funcionó")
- `project` — estado actual, deadlines, decisiones, por qué se hace algo
- `reference` — URLs externas (Linear, Grafana, docs)

**Búsqueda manual** (SIEMPRE ANTES de implementar features nuevas):
```
mcp__plugin_claude-mem_mcp-search__search(
  query: "authentication architecture"  // qué buscas
  limit: 5                               // resultados
  project: "alkilokmp"                   // opcional
)
```
→ Retorna IDs de observaciones relevantes

```
mcp__plugin_claude-mem_mcp-search__timeline(
  anchor: <observation_id>,             // ID de arriba
  depth_before: 3,
  depth_after: 3
)
```
→ Contexto alrededor de una decisión (qué pasó antes/después)

```
mcp__plugin_claude-mem_mcp-search__get_observations(
  ids: [123, 456]                       // IDs de search()
)
```
→ Contenido completo de observaciones

**Cuándo usar**:
- **Antes de arquitectura nueva**: `search("auth", "navigation", "state management")`
- **Si hay contradicciones**: `timeline()` para ver decisiones previas
- **Al revisar código viejo**: `get_observations()` para entender el "por qué"

---

## Workflow Recomendado

1. **Nueva feature**: `search("feature-name")` en claude-mem → encontrar contexto previo
2. **Investigación**: `ctx_batch_execute(commands + queries)` → research loop compacto
3. **Análisis**: `ctx_execute_file()` o `ctx_execute()` → procesar datos sin Bash
4. **Guardado**: claude-mem graba automáticamente tus decisiones (no acción requierida)

---

Para detalles completos del design system, ver `.impeccable.md`
Para contexto de arquitectura y desarrollo, ver `docs/CODEX_WORKING_CONTEXT.md`

---

Para detalles completos del design system, ver `.impeccable.md`
Para contexto de arquitectura y desarrollo, ver `docs/CODEX_WORKING_CONTEXT.md`
