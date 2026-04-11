package dev.andyromero.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Primary
    primary = AlkiloColors.Terracotta,
    onPrimary = AlkiloColors.TextOnPrimary,
    primaryContainer = AlkiloColors.TerracottaLight,
    onPrimaryContainer = AlkiloColors.TextPrimary,

    // Secondary (usando tonos del primary para consistencia)
    secondary = AlkiloColors.TerracottaDark,
    onSecondary = AlkiloColors.TextOnPrimary,
    secondaryContainer = AlkiloColors.SurfaceTertiary,
    onSecondaryContainer = AlkiloColors.TextPrimary,

    // Tertiary
    tertiary = AlkiloColors.TerracottaLight,
    onTertiary = AlkiloColors.TextPrimary,

    // Background & Surface
    background = AlkiloColors.SurfacePrimary,
    onBackground = AlkiloColors.TextPrimary,
    surface = AlkiloColors.SurfacePrimary,
    onSurface = AlkiloColors.TextPrimary,
    surfaceVariant = AlkiloColors.SurfaceSecondary,
    onSurfaceVariant = AlkiloColors.TextSecondary,
    surfaceContainerHighest = AlkiloColors.SurfaceSecondary,
    surfaceContainerHigh = AlkiloColors.SurfaceSecondary,
    surfaceContainer = AlkiloColors.SurfacePrimary,
    surfaceContainerLow = AlkiloColors.SurfacePrimary,
    surfaceContainerLowest = Color.White,

    // Outline
    outline = AlkiloColors.Border,
    outlineVariant = AlkiloColors.Border.copy(alpha = 0.5f),

    // Error
    error = AlkiloColors.Error,
    onError = AlkiloColors.TextOnPrimary,
    errorContainer = AlkiloColors.ErrorLight,
    onErrorContainer = AlkiloColors.Error,

    // Inverse (para snackbars, etc.)
    inverseSurface = AlkiloColors.TextPrimary,
    inverseOnSurface = AlkiloColors.SurfacePrimary,
    inversePrimary = AlkiloColors.TerracottaLight,
)

private val DarkColorScheme = darkColorScheme(
    // Primary - desaturar ligeramente en dark mode
    primary = AlkiloColors.TerracottaLight,
    onPrimary = AlkiloColors.TextPrimary,
    primaryContainer = AlkiloColors.TerracottaDark,
    onPrimaryContainer = AlkiloColors.TextPrimaryDark,

    // Secondary
    secondary = AlkiloColors.Terracotta,
    onSecondary = AlkiloColors.TextOnPrimary,
    secondaryContainer = AlkiloColors.SurfaceTertiaryDark,
    onSecondaryContainer = AlkiloColors.TextPrimaryDark,

    // Tertiary
    tertiary = AlkiloColors.TerracottaLight,
    onTertiary = AlkiloColors.TextPrimary,

    // Background & Surface - nunca negro puro
    background = AlkiloColors.SurfacePrimaryDark,
    onBackground = AlkiloColors.TextPrimaryDark,
    surface = AlkiloColors.SurfacePrimaryDark,
    onSurface = AlkiloColors.TextPrimaryDark,
    surfaceVariant = AlkiloColors.SurfaceSecondaryDark,
    onSurfaceVariant = AlkiloColors.TextSecondaryDark,
    surfaceContainerHighest = AlkiloColors.SurfaceTertiaryDark,
    surfaceContainerHigh = AlkiloColors.SurfaceSecondaryDark,
    surfaceContainer = AlkiloColors.SurfaceSecondaryDark,
    surfaceContainerLow = AlkiloColors.SurfacePrimaryDark,
    surfaceContainerLowest = AlkiloColors.SurfacePrimaryDark,

    // Outline
    outline = AlkiloColors.BorderDark,
    outlineVariant = AlkiloColors.BorderDark.copy(alpha = 0.5f),

    // Error - más brillante en dark mode para visibilidad
    error = AlkiloColors.ErrorDark,
    onError = AlkiloColors.TextPrimary,
    errorContainer = AlkiloColors.Error.copy(alpha = 0.2f),
    onErrorContainer = AlkiloColors.ErrorDark,

    // Inverse
    inverseSurface = AlkiloColors.SurfacePrimary,
    inverseOnSurface = AlkiloColors.TextPrimary,
    inversePrimary = AlkiloColors.Terracotta,
)

@Composable
fun AlkiloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AlkiloTypography,
        content = content,
    )
}
