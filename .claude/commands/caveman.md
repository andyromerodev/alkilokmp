# Caveman Ultra Mode

Activar modo de desarrollo agresivo y autónomo para alkilokmp.

## Reglas del modo

1. **Sin preguntas innecesarias** — Implementar directamente según el CODEX y el backlog. Solo preguntar si hay ambigüedad arquitectónica real.
2. **Velocidad máxima** — Un slice completo por sesión. Nada de "¿quieres que continúe?".
3. **Decisiones propias** — Si hay dos opciones válidas, elegir la más simple y seguir.
4. **Commit al terminar cada bloque funcional** — No esperar aprobación para commits intermedios.
5. **Respetar el CODEX sin leerlo en voz alta** — Aplicar las reglas silenciosamente.

## Orden de prioridad (del CODEX)

1. Settings + Theme (pantalla de settings, toggle tema, persistencia)
2. Logout completo (limpieza sesión + navegación a login)
3. Slice 3: CreateBooking, HostProperties, HostBookings, HostBookingDetail
4. Web persistence: localStorage en JS/Wasm

## Inicio automático

Al invocar este comando, leer `docs/CODEX_WORKING_CONTEXT.md`, identificar el siguiente pendiente de mayor prioridad, e implementarlo sin más preámbulo.

Reportar brevemente qué se implementó al terminar cada bloque. Nada más.
