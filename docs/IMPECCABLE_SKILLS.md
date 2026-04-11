# Impeccable Skills - Guia de Comandos

Suite de skills para crear interfaces frontend distintivas y de alta calidad, evitando la estetica generica de "AI slop".

---

## Comando Principal

### `/impeccable [craft|teach|extract]`
**Skill principal** para crear interfaces frontend de grado produccion.

| Argumento | Descripcion |
|-----------|-------------|
| `craft` | Planifica y construye (shape-then-build) |
| `teach` | Configura el contexto de diseno del proyecto |
| `extract` | Extrae componentes y tokens reutilizables al design system |

**Cuando usar:** Al construir componentes web, paginas, posters o aplicaciones. Siempre ejecutar primero si no existe contexto de diseno.

```
/impeccable teach  → Configurar contexto inicial
/impeccable craft  → Planificar y construir feature
/impeccable extract → Extraer componentes reutilizables
```

---

## Skills de Evaluacion

### `/audit [area]`
Ejecuta chequeos tecnicos de calidad: accesibilidad, performance, theming, responsive y anti-patterns.

**Genera:** Reporte con scores 0-4 y severidad P0-P3.

**Cuando usar:** "accessibility check", "performance audit", "quality review"

---

### `/critique [area]`
Evalua diseno desde perspectiva UX: jerarquia visual, arquitectura de informacion, carga cognitiva, resonancia emocional.

**Incluye:** Scoring cuantitativo, testing basado en personas, deteccion automatica de anti-patterns.

**Cuando usar:** "review this", "critique", "evaluate", "give feedback"

---

## Skills de Planificacion

### `/shape [feature]`
Planifica UX/UI antes de escribir codigo. Ejecuta entrevista de discovery estructurada y produce un design brief.

**Output:** Design brief para guiar implementacion.

**Cuando usar:** Fase de planificacion, antes de cualquier codigo.

---

## Skills de Mejora Visual

### `/bolder [target]`
Amplifica disenos seguros o aburridos para hacerlos mas impactantes y memorables.

**Cuando usar:** "looks bland", "generic", "too safe", "lacks personality", "more impact"

---

### `/quieter [target]`
Reduce intensidad visual en disenos demasiado agresivos o sobreestimulantes.

**Cuando usar:** "too bold", "too loud", "overwhelming", "aggressive", "garish", "calmer"

---

### `/colorize [target]`
Agrega color estrategico a features monocromaticos o sin interes visual.

**Cuando usar:** "looks gray", "dull", "lacking warmth", "more color", "more vibrant"

---

### `/typeset [target]`
Mejora tipografia: fuentes, jerarquia, sizing, peso y legibilidad.

**Cuando usar:** "fonts", "type", "readability", "text hierarchy", "sizing looks off"

---

### `/layout [target]`
Mejora layout, spacing y ritmo visual. Corrige grids monotonos y jerarquia debil.

**Cuando usar:** "layout feels off", "spacing issues", "visual hierarchy", "crowded UI", "alignment"

---

## Skills de Refinamiento

### `/polish [target]`
Pase final de calidad: alineacion, spacing, consistencia y micro-detalles antes de ship.

**Cuando usar:** "polish", "finishing touches", "pre-launch review", "looks off", "good to great"

---

### `/distill [target]`
Elimina complejidad innecesaria. Gran diseno es simple, poderoso y limpio.

**Cuando usar:** "simplify", "declutter", "reduce noise", "remove elements", "cleaner"

---

### `/clarify [target]`
Mejora copy de UX: mensajes de error, microcopy, labels e instrucciones.

**Cuando usar:** "confusing text", "unclear labels", "bad error messages", "hard-to-follow"

---

## Skills de Interaccion

### `/animate [target]`
Agrega animaciones y micro-interacciones con proposito que mejoran usabilidad y deleitan.

**Cuando usar:** "add animation", "transitions", "micro-interactions", "motion design", "hover effects", "feel alive"

---

### `/delight [target]`
Agrega momentos de alegria, personalidad y toques inesperados que hacen interfaces memorables.

**Cuando usar:** "add polish", "personality", "animations", "micro-interactions", "delight", "fun"

---

## Skills Tecnicos

### `/adapt [target] [context]`
Adapta disenos para diferentes pantallas, dispositivos, contextos o plataformas.

**Contextos:** mobile, tablet, print, etc.

**Cuando usar:** "responsive design", "mobile layouts", "breakpoints", "viewport", "cross-device"

---

### `/harden [target]`
Prepara interfaces para produccion: error handling, empty states, onboarding, i18n, text overflow, edge cases.

**Cuando usar:** "harden", "production-ready", "edge cases", "error states", "empty states", "overflow", "i18n"

---

### `/optimize [target]`
Diagnostica y corrige performance: loading, rendering, animaciones, imagenes, bundle size.

**Cuando usar:** "slow", "laggy", "janky", "performance", "bundle size", "load time", "faster"

---

### `/overdrive [target]`
Lleva interfaces mas alla de limites convencionales: shaders, spring physics, scroll-driven, 60fps.

**Cuando usar:** "wow", "impress", "go all-out", "extraordinary"

**Nota:** Siempre propone 2-3 direcciones antes de implementar.

---

## Resumen Rapido

| Skill | Proposito | Trigger Keywords |
|-------|-----------|------------------|
| `/impeccable` | Skill principal, contexto | build, craft, teach |
| `/audit` | Chequeo tecnico | accessibility, performance, quality |
| `/critique` | Evaluacion UX | review, critique, evaluate, feedback |
| `/shape` | Planificar UX/UI | planning, before code, design brief |
| `/bolder` | Mas impacto | bland, generic, safe, personality |
| `/quieter` | Menos intenso | loud, overwhelming, aggressive, calmer |
| `/colorize` | Agregar color | gray, dull, warmth, vibrant |
| `/typeset` | Tipografia | fonts, type, readability, hierarchy |
| `/layout` | Layout/spacing | spacing, alignment, hierarchy, crowded |
| `/polish` | Refinamiento final | polish, finishing, pre-launch, off |
| `/distill` | Simplificar | simplify, declutter, noise, cleaner |
| `/clarify` | Mejorar copy | confusing, unclear, error messages |
| `/animate` | Animaciones | animation, transitions, motion, alive |
| `/delight` | Momentos de alegria | personality, delight, fun, memorable |
| `/adapt` | Responsive | responsive, mobile, breakpoints, devices |
| `/harden` | Edge cases | production, errors, empty, overflow |
| `/optimize` | Performance | slow, laggy, performance, bundle |
| `/overdrive` | Efectos avanzados | wow, impress, extraordinary |

---

## Flujo Recomendado

```
1. /impeccable teach    → Establecer contexto del proyecto
2. /shape [feature]     → Planificar UX/UI
3. /impeccable craft    → Construir
4. /critique            → Evaluar
5. /polish              → Refinamiento final
6. /audit               → Chequeo tecnico pre-ship
```

---

## Notas Importantes

- **Contexto requerido:** Todos los skills requieren contexto de diseno. Si no existe `.impeccable.md`, ejecutar `/impeccable teach` primero.
- **Anti-AI slop:** Los skills estan disenados para evitar estetica generica de AI (gradientes cyan/purple, glassmorphism, grid de cards identicos, etc.)
- **Accesibilidad:** Siempre respetar `prefers-reduced-motion` y WCAG guidelines.
