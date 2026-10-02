package com.osm.frogedit.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.layout.Row
import com.osm.frogedit.theme.LocalButtonGradient

/**
 * A Material [Button] whose background is a gradient instead of a solid color. The button keeps
 * Material's ripple, focus, shape and disabled behaviour; only the fill is replaced.
 */
@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brush: Brush = LocalButtonGradient.current,
    content: @Composable RowScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val background = if (enabled) brush else SolidColor(colors.onSurface.copy(alpha = 0.12f))

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colors.onPrimary,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            Modifier
                .background(background)
                .defaultMinSize(ButtonDefaults.MinWidth, ButtonDefaults.MinHeight)
                .padding(ButtonDefaults.ContentPadding),
            contentAlignment = Alignment.Center
        ) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Row(verticalAlignment = Alignment.CenterVertically, content = content)
            }
        }
    }
}
