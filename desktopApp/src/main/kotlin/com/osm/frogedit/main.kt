package com.osm.frogedit

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.osm.frogedit.composables.AboutDialog
import com.osm.frogedit.composables.SettingsDialog
import com.osm.frogedit.files.EditorDocument
import com.osm.frogedit.files.FileActions
import com.osm.frogedit.settings.SettingsStore
import frogedit.shared.generated.resources.Res
import frogedit.shared.generated.resources.frog_edit
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(SettingsStore.load()) }

    val document = remember { EditorDocument() }
    // Closing the window goes through the unsaved-changes prompt, which needs the window
    var exitRequested by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Window(
        onCloseRequest = { exitRequested = true },
        title = (if (document.isModified) "*" else "") + "${document.displayName} - Frog Edit",
        icon = painterResource(Res.drawable.frog_edit)
    ) {
        val fileActions = remember(window) { FileActions(document, window) }

        LaunchedEffect(exitRequested) {
            if (exitRequested) {
                if (fileActions.confirmCanDiscard()) exitApplication()
                exitRequested = false
            }
        }

        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item(
                    "New",
                    onClick = { scope.launch { fileActions.newDocument() } },
                    shortcut = KeyShortcut(Key.N, ctrl = true)
                )
                Item(
                    "Open…",
                    onClick = { scope.launch { fileActions.open() } },
                    shortcut = KeyShortcut(Key.O, ctrl = true)
                )
                Item(
                    "Save",
                    onClick = { scope.launch { fileActions.save() } },
                    shortcut = KeyShortcut(Key.S, ctrl = true)
                )
                Item(
                    "Save As…",
                    onClick = { scope.launch { fileActions.saveAs() } },
                    shortcut = KeyShortcut(Key.S, ctrl = true, shift = true)
                )
                Separator()
                Item(
                    "Settings…",
                    onClick = { showSettings = true },
                    shortcut = KeyShortcut(Key.Comma, ctrl = true)
                )
                Separator()
                Item("Exit", onClick = { exitRequested = true })
            }
            Menu("View") {
                var dark by remember { mutableStateOf(false) }
                CheckboxItem("Dark mode", checked = dark, onCheckedChange = { dark = it })

                Menu("Zoom") {               // nested submenu
                    RadioButtonItem("100%", selected = true, onClick = {})
                    RadioButtonItem("150%", selected = false, onClick = {})
                }
            }
            Menu("Help") {
                Item("About", onClick = { showAbout = true })
            }
        }

        App(settings = settings, editorState = document.textState)

        if (showSettings) {
            SettingsDialog(
                settings = settings,
                onSettingsChange = {
                    settings = it
                    SettingsStore.save(it)
                },
                onClose = { showSettings = false }
            )
        }

        if (showAbout) {
            AboutDialog(onClose = { showAbout = false })
        }
    }
}