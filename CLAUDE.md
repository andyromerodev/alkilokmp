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

- **context-mode** — routes large-output commands (build, tests, git log, grep, APIs) through sandbox. Only summaries enter context. Saves 60–90% tokens. Automatic via hooks. Use `ctx_execute`/`ctx_execute_file` instead of Bash for anything that reads/lists/analyzes data.
- **claude-mem** — persists observations across sessions. Worker at `http://localhost:37777`. Use MCP tools `search`/`get_observations`/`timeline` to query project history before implementing features. See `docs/CONTEXT_MODE_GUIDE.md`.

---

Para detalles completos del design system, ver `.impeccable.md`
Para contexto de arquitectura y desarrollo, ver `docs/CODEX_WORKING_CONTEXT.md`
