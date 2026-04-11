package dev.andyromero.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Alkilo Color System
 *
 * Minimalismo cálido con neutros arena/terracota.
 * Tinted neutrals hacia el hue de terracota (~45) para cohesión.
 */
object AlkiloColors {

    // ═══════════════════════════════════════════════════════════════
    // PRIMARY - Terracota cálido
    // ═══════════════════════════════════════════════════════════════
    val Terracotta = Color(0xFFB5704F)       // oklch(55% 0.12 45) - Primary
    val TerracottaDark = Color(0xFF8B5A3E)   // oklch(45% 0.10 45) - Pressed/Hover
    val TerracottaLight = Color(0xFFD4A088)  // oklch(72% 0.08 45) - Subtle accent

    // ═══════════════════════════════════════════════════════════════
    // SURFACES - Light Theme (blancos cremosos, beiges)
    // Tinted hacia hue 50 (arena/terracota) con chroma muy bajo
    // ═══════════════════════════════════════════════════════════════
    val SurfacePrimary = Color(0xFFFCFAF7)     // oklch(98% 0.008 50) - Main background
    val SurfaceSecondary = Color(0xFFF5F1EB)  // oklch(95% 0.012 50) - Cards, inputs
    val SurfaceTertiary = Color(0xFFEDE7DD)   // oklch(92% 0.015 50) - Hover states

    // ═══════════════════════════════════════════════════════════════
    // SURFACES - Dark Theme
    // Same hue (50), vary lightness, reduce chroma slightly
    // ═══════════════════════════════════════════════════════════════
    val SurfacePrimaryDark = Color(0xFF1C1915)   // oklch(15% 0.01 50)
    val SurfaceSecondaryDark = Color(0xFF262320) // oklch(20% 0.01 50)
    val SurfaceTertiaryDark = Color(0xFF312D29)  // oklch(25% 0.01 50)

    // ═══════════════════════════════════════════════════════════════
    // TEXT - Light Theme
    // Marrones oscuros (mejor contraste que negro puro)
    // ═══════════════════════════════════════════════════════════════
    val TextPrimary = Color(0xFF2D2520)        // oklch(22% 0.02 50) - Headings
    val TextSecondary = Color(0xFF5C524A)      // oklch(42% 0.02 50) - Body
    val TextTertiary = Color(0xFF8A7F74)       // oklch(58% 0.015 50) - Captions, placeholders
    val TextOnPrimary = Color(0xFFFFFBF7)      // oklch(98% 0.005 50) - Text on terracotta

    // ═══════════════════════════════════════════════════════════════
    // TEXT - Dark Theme
    // ═══════════════════════════════════════════════════════════════
    val TextPrimaryDark = Color(0xFFF5F0E8)    // oklch(95% 0.01 50)
    val TextSecondaryDark = Color(0xFFB8AFA3)  // oklch(75% 0.015 50)
    val TextTertiaryDark = Color(0xFF7A7269)   // oklch(52% 0.01 50)

    // ═══════════════════════════════════════════════════════════════
    // BORDERS & DIVIDERS
    // ═══════════════════════════════════════════════════════════════
    val Border = Color(0xFFE0D8CE)             // oklch(88% 0.015 50) - Light
    val BorderDark = Color(0xFF3D3833)         // oklch(28% 0.01 50) - Dark

    // ═══════════════════════════════════════════════════════════════
    // SEMANTIC - Error (rojo cálido, no rojo puro)
    // ═══════════════════════════════════════════════════════════════
    val Error = Color(0xFFC75146)              // oklch(52% 0.15 28)
    val ErrorLight = Color(0xFFFCEDEB)         // oklch(95% 0.02 28) - Error background
    val ErrorDark = Color(0xFFE07A6E)          // oklch(65% 0.12 28) - Dark theme error

    // ═══════════════════════════════════════════════════════════════
    // SEMANTIC - Success (verde oliva, no verde saturado)
    // ═══════════════════════════════════════════════════════════════
    val Success = Color(0xFF5D8B5A)            // oklch(55% 0.10 145)
    val SuccessLight = Color(0xFFEDF5EC)       // oklch(95% 0.02 145)
}
