package com.osm.frogedit.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.osm.frogedit.settings.EditorSettings
import com.osm.frogedit.settings.parseHexColor
import com.osm.frogedit.settings.toHexString
import com.osm.frogedit.theme.FrogEditTheme
import frogedit.shared.generated.resources.Res
import frogedit.shared.generated.resources.frog_edit
import org.jetbrains.compose.resources.painterResource

/** Changes are applied immediately via [onSettingsChange]; there is no separate save step. */
@Composable
fun SettingsDialog(
    settings: EditorSettings,
    onSettingsChange: (EditorSettings) -> Unit,
    onClose: () -> Unit
) {
    DialogWindow(
        onCloseRequest = onClose,
        state = rememberDialogState(size = DpSize(500.dp, 680.dp)),
        title = "Settings",
        icon = painterResource(Res.drawable.frog_edit),
        resizable = true,
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                onClose()
                true
            } else {
                false
            }
        }
    ) {
        FrogEditTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().padding(24.dp)) {
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        Text("Editor", style = MaterialTheme.typography.titleMedium)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        SettingSwitch(
                            title = "Show line numbers",
                            description = "Display line numbers in a gutter beside the text.",
                            checked = settings.showLineNumbers,
                            onCheckedChange = { onSettingsChange(settings.copy(showLineNumbers = it)) }
                        )
                        SettingSwitch(
                            title = "Show status bar",
                            description = "Display cursor position and text statistics at the bottom.",
                            checked = settings.showStatusBar,
                            onCheckedChange = { onSettingsChange(settings.copy(showStatusBar = it)) }
                        )

                        Spacer(Modifier.height(16.dp))
                        Text("Colors", style = MaterialTheme.typography.titleMedium)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        ColorSetting(
                            title = "Editor background",
                            color = settings.editorBackground,
                            presets = BackgroundPresets,
                            onColorChange = { onSettingsChange(settings.copy(editorBackground = it)) }
                        )
                        ColorSetting(
                            title = "Text color",
                            color = settings.textColor,
                            presets = TextColorPresets,
                            onColorChange = { onSettingsChange(settings.copy(textColor = it)) }
                        )
                        EditorPreview(settings)
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        GradientButton(onClick = onClose) { Text("Close") }
                    }
                }
            }
        }
    }
}

/** A labelled switch row; the whole row is clickable. */
@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Row handles the toggle so clicks on the label work too
        Switch(checked = checked, onCheckedChange = null)
    }
}

private val BackgroundPresets = listOf(
    Color(0xFFFFFFFF), // White
    Color(0xFFFAF8F0), // Paper
    Color(0xFFF1F8E9), // Mint
    Color(0xFFFFF8E1), // Cream
    Color(0xFFE3F2FD), // Sky
    Color(0xFF263238), // Slate
    Color(0xFF1E1E1E), // Charcoal
    Color(0xFF000000), // Black
)

private val TextColorPresets = listOf(
    Color(0xFF000000), // Black
    Color(0xFF212121), // Graphite
    Color(0xFF1B5E20), // Forest
    Color(0xFF0D47A1), // Navy
    Color(0xFF4E342E), // Brown
    Color(0xFFB71C1C), // Crimson
    Color(0xFFE0E0E0), // Light gray
    Color(0xFFFFFFFF), // White
)

/** Swatches, a hex input and a "Default" option for one color setting. */
@Composable
private fun ColorSetting(
    title: String,
    color: Color,
    presets: List<Color>,
    onColorChange: (Color) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            presets.forEach { preset ->
                ColorSwatch(preset, selected = preset == color, onClick = { onColorChange(preset) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Re-seed the field whenever the color changes from outside (swatch, Default)
            var hex by remember(color) { mutableStateOf(if (color.isSpecified) color.toHexString() else "") }
            val isValid = hex.isEmpty() || parseHexColor(hex) != null
            OutlinedTextField(
                value = hex,
                onValueChange = { input ->
                    hex = input
                    parseHexColor(input)?.let(onColorChange)
                },
                modifier = Modifier.width(160.dp),
                singleLine = true,
                isError = !isValid,
                label = { Text("Hex") },
                placeholder = { Text("#RRGGBB") }
            )
            TextButton(onClick = { onColorChange(Color.Unspecified) }, enabled = color.isSpecified) {
                Text("Default")
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) colors.primary else colors.outline,
                shape = CircleShape
            )
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
    )
}

/** Shows how the chosen colors look together, so unreadable combinations are obvious. */
@Composable
private fun EditorPreview(settings: EditorSettings) {
    val colors = MaterialTheme.colorScheme
    Text("Preview", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
    Box(
        Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            // Unspecified background matches the editor's default look (window background shows through)
            .background(settings.editorBackground.takeOrElse { colors.primaryContainer })
            .border(1.dp, colors.outline)
            .padding(12.dp)
    ) {
        Text(
            "The quick brown frog\njumps over the lazy log.",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            color = settings.textColor.takeOrElse { colors.onSurface }
        )
    }
}
