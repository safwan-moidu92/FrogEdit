package com.osm.frogedit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.osm.frogedit.composables.GradientButton
import com.osm.frogedit.theme.FrogEditTheme
import frogedit.shared.generated.resources.Res
import frogedit.shared.generated.resources.frog_edit
import org.jetbrains.compose.resources.painterResource

// Keep in sync with packageVersion in desktopApp/build.gradle.kts
private const val AppVersion = "1.0.0"

@Composable
fun AboutDialog(onClose: () -> Unit) {
    DialogWindow(
        onCloseRequest = onClose,
        state = rememberDialogState(size = DpSize(360.dp, 400.dp)),
        title = "About Frog Edit",
        icon = painterResource(Res.drawable.frog_edit),
        resizable = false,
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
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painterResource(Res.drawable.frog_edit),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        "Frog Edit",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Version $AppVersion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Text(
                        "A simple, lightweight text editor.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Built with Kotlin and Compose Multiplatform\n" +
                            "Running on Java ${System.getProperty("java.version")} · " +
                            System.getProperty("os.name"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.weight(1f))
                    GradientButton(onClick = onClose) { Text("Close") }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
