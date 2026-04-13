# Context Mode — Guía de Uso

## ¿Qué es y para qué sirve?

Claude tiene una **ventana de contexto limitada** (tokens). Cuando un comando produce output grande (logs, tests, JSON, git diff, etc.) y ese output entra directo al contexto, consume tokens valiosos que luego no están disponibles para razonamiento y código.

**Context-mode intercepta ese output antes de que llegue al contexto**, lo procesa en un sandbox, y solo devuelve un resumen pequeño. El ahorro típico es **60–90% de tokens**.

```
Sin context-mode:  comando → output enorme → contexto  (caro)
Con context-mode:  comando → sandbox → solo findings → contexto  (barato)
```

---

## Cómo funciona en la práctica

Context-mode está activo **automáticamente** via hooks de Claude Code. No necesitas hacer nada especial — Claude decide cuándo usarlo. Pero entender cuándo se activa te ayuda a pedírselo explícitamente si hace falta.

---

## Regla principal

| Situación | Herramienta correcta |
|-----------|---------------------|
| Mutar archivos (`mkdir`, `mv`, `rm`) | Bash directo |
| Commits, push, checkout | Bash directo |
| **Cualquier comando que lea/liste/analice datos** | `ctx_execute` (sandbox) |
| **Leer un archivo grande para analizar** | `ctx_execute_file` (sandbox) |
| **Buscar en docs indexadas** | `ctx_search` |
| **Fetch de documentación web** | `ctx_fetch_and_index` |

---

## Casos comunes en este proyecto

### Ver commits recientes sin inundar el contexto
En vez de `git log` por Bash, Claude usará:
```shell
# ctx_execute con shell
git log --oneline -30
```
Solo el resumen entra al contexto.

### Analizar errores de build de Gradle
```shell
# ctx_execute_file sobre el log de build
# Claude extrae solo los errores, no el log completo
```

### Revisar tests
```shell
./gradlew :composeApp:testDebugUnitTest 2>&1
echo "EXIT=$?"
```
En vez de volcar miles de líneas al contexto, Claude imprime: `12 tests, 1 failed: [nombre del test]`.

### Buscar en la base de código
```javascript
// ctx_execute con JS/shell para buscar patrones
// Devuelve solo los matches relevantes, no el output de grep
```

---

## Comandos de gestión

| Comando | Qué hace |
|---------|----------|
| `/ctx-stats` | Muestra cuántos tokens se ahorraron en esta sesión |
| `/ctx-doctor` | Diagnóstico: hooks, FTS5, versión |
| `/ctx-upgrade` | Actualiza context-mode desde GitHub |
| `/ctx-purge` | Borra la base de conocimiento indexada (irreversible) |

### Ver estadísticas de ahorro
Escribe `ctx stats` o `/ctx-stats` en cualquier momento para ver:
- Tokens consumidos
- Tokens ahorrados
- % de reducción

---

## Flujo con Playwright / UI (si se usa en el futuro)

Si se integra Playwright para tests de UI:

```
# MAL — inunda el contexto con 135K tokens
browser_snapshot()

# BIEN — procesa en sandbox
browser_snapshot(filename: "/tmp/snap.md")
→ ctx_execute_file(path: "/tmp/snap.md", ...)  # solo los findings entran al contexto
```

---

## Anti-patrones a evitar

- `cat archivo-grande.json` por Bash → usa `ctx_execute_file`
- `gh pr list` sin filtro por Bash → usa `ctx_execute` con `--jq`
- `./gradlew build` por Bash → usa `ctx_execute` para capturar y resumir
- `curl http://api/endpoint` por Bash → usa `ctx_execute` con fetch JS

---

## Resumen en una línea

> **Todo comando que produce output grande va por `ctx_execute` o `ctx_execute_file`, no por Bash directo.**

Claude lo maneja automáticamente. Puedes monitorear el ahorro con `/ctx-stats`.
