package com.osm.frogedit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Frog green palette
private val Green10 = Color(0xFF002106)
private val Green20 = Color(0xFF00390F)
private val Green30 = Color(0xFF005319)
private val Green40 = Color(0xFF2E7D32)
private val Green80 = Color(0xFF81C784)
private val Green90 = Color(0xFFC8E6C9)
private val Green95 = Color(0xFFE8F5E9)
private val Green50 = Color(0xFF43A047)
private val Green60 = Color(0xFF66BB6A)
private val Green70 = Color(0xFFA5D6A7)
private val Lime40 = Color(0xFF558B2F)
private val Lime80 = Color(0xFFAED581)
private val Lime90 = Color(0xFFDCEDC8)

private val LightColors = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Lime40,
    onSecondary = Color.White,
    secondaryContainer = Lime90,
    onSecondaryContainer = Green10,
    surfaceVariant = Green95,
)

private val DarkColors = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = Lime80,
    onSecondary = Green20,
    secondaryContainer = Green30,
    onSecondaryContainer = Lime90,
)

private val LightButtonGradient = Brush.horizontalGradient(listOf(Green30, Green40, Green60))
private val DarkButtonGradient = Brush.horizontalGradient(listOf(Green50, Green80, Green70))

/** Background gradient for [com.osm.frogedit.composables.GradientButton]. */
val LocalButtonGradient = staticCompositionLocalOf { LightButtonGradient }

@Composable
fun FrogEditTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalButtonGradient provides if (darkTheme) DarkButtonGradient else LightButtonGradient
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content
        )
    }
}
