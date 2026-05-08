package com.morchon.lain.ui.theme

import androidx.compose.ui.graphics.Color

val OpenGreen = Color(0xFF10B981)
val MiseOrange = Color(0xFFF97316)
val BackgroundDark = Color(0xFF18181B)
val SurfaceDark = Color(0xFF27272A)
val OnBackgroundDark = Color(0xFFFAFAFA)
val OnSurfaceDark = Color(0xFFFAFAFA)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = OpenGreen,
    secondary = MiseOrange,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    primaryContainer = OpenGreen.copy(alpha = 0.2f),
    onPrimaryContainer = OpenGreen,
    secondaryContainer = MiseOrange.copy(alpha = 0.2f),
    onSecondaryContainer = MiseOrange
)
