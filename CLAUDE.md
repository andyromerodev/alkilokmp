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

Para detalles completos del design system, ver `.impeccable.md`
Para contexto de arquitectura y desarrollo, ver `docs/CODEX_WORKING_CONTEXT.md`
