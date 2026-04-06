---
name: kmp-supabase-multiplatform
description: Integrar Supabase en Kotlin Multiplatform con configuración y networking por target. Usar cuando se implemente auth/data compartidos en commonMain y se necesite evitar mocks en login, resolver URL/key por provider y soportar Android+iOS+Desktop+Web.
---

# KMP Supabase Multiplatform

## Configuración compartida
1. Definir `PlatformConfigProvider` en `commonMain`.
2. Cargar `supabase.url` y `supabase.anonKey` desde fuente de build segura.
3. Bloquear ejecución con error explícito si falta configuración (sin login simulado).

## Cliente y repositorio
1. Crear repositorio en `commonMain` con contratos de dominio.
2. Implementar mappers DTO -> dominio en común.
3. Inicializar cliente con engine disponible por target.

## Reglas de seguridad
1. No hardcodear keys en código fuente.
2. No exponer secrets en logs.
3. Distinguir errores de red, auth y configuración.

## Validaciones mínimas
1. Pruebas de casos de uso/auth en `commonTest`.
2. Smoke de inicialización del cliente por target.
3. Verificar permisos Android (`INTERNET`, `ACCESS_NETWORK_STATE`) y equivalentes de plataforma.

