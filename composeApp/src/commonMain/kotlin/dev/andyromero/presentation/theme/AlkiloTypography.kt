package dev.andyromero.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Alkilo Typography
 *
 * Scale con ratio ~1.25 para jerarquía clara.
 * Usando system fonts para performance (datos limitados en Cuba).
 *
 * Scale:
 * - Display: 32sp (headings principales)
 * - Headline: 24sp
 * - Title: 20sp
 * - Body: 16sp (base)
 * - Label: 14sp
 * - Caption: 12sp
 */
val AlkiloTypography = Typography(
    // ═══════════════════════════════════════════════════════════════
    // DISPLAY - Para títulos hero, pantallas de auth
    // ═══════════════════════════════════════════════════════════════
    displayLarge = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp,
    ),
    displaySmall = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),

    // ═══════════════════════════════════════════════════════════════
    // HEADLINE - Secciones, headers de pantalla
    // ═══════════════════════════════════════════════════════════════
    headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),

    // ═══════════════════════════════════════════════════════════════
    // TITLE - Subtítulos, card headers
    // ═══════════════════════════════════════════════════════════════
    titleLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // ═══════════════════════════════════════════════════════════════
    // BODY - Texto principal, descripciones
    // ═══════════════════════════════════════════════════════════════
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,  // 1.5 ratio para legibilidad
        letterSpacing = 0.15.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    ),

    // ═══════════════════════════════════════════════════════════════
    // LABEL - Botones, chips, labels de form
    // ═══════════════════════════════════════════════════════════════
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp,
    ),
)
