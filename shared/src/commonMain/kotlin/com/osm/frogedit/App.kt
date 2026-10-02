package com.osm.frogedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.osm.frogedit.composables.StatusBar
import com.osm.frogedit.composables.TextEditor
import com.osm.frogedit.settings.EditorSettings
import com.osm.frogedit.theme.FrogEditTheme
import org.jetbrains.compose.resources.painterResource

import frogedit.shared.generated.resources.Res
import frogedit.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App(settings: EditorSettings = EditorSettings()) {
    FrogEditTheme {
        var showContent by remember { mutableStateOf(false) }
        val editorState = rememberTextFieldState()
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val list = listOf("Safwan", "Moidu")

            /*
            ContextMenuArea(items = {
                listOf(
                    ContextMenuItem("Copy") {  },
                    ContextMenuItem("Rename") {  },
                    ContextMenuItem("Delete") {  }
                )
            }) {
                Text("Select Text", Modifier.fillMaxWidth().padding(8.dp))
            }*/
            TextEditor(
                modifier = Modifier.weight(1f),
                state = editorState,
                showLineNumbers = settings.showLineNumbers,
                backgroundColor = settings.editorBackground,
                textColor = settings.textColor
            )
            if (settings.showStatusBar) {
                StatusBar(state = editorState)
            }

        }
    }
}

