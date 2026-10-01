package com.osm.frogedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import frogedit.shared.generated.resources.Res
import frogedit.shared.generated.resources.frog_edit
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    var showAbout by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Frog Edit - Text Editor",
        icon = painterResource(Res.drawable.frog_edit)
    ) {
        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item(
                    "New",
                    onClick = { /* ... */ },
                    shortcut = KeyShortcut(Key.N, ctrl = true)
                )
                Item("Open…", onClick = { /* ... */ })
                Separator()
                Item("Exit", onClick = ::exitApplication)
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

        App()
    }
}