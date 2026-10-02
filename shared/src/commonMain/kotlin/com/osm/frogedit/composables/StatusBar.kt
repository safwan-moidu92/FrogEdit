package com.osm.frogedit.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Statistics about the editor's text and caret, shown in the [StatusBar]. */
data class EditorStats(
    val lines: Int,
    val words: Int,
    val characters: Int,
    val charactersNoSpaces: Int,
    /** 1-based line of the caret. */
    val caretLine: Int,
    /** 1-based column of the caret. */
    val caretColumn: Int,
    val selectedCharacters: Int,
    val selectedLines: Int
)

fun computeEditorStats(text: CharSequence, selection: TextRange): EditorStats {
    var lines = 1
    var words = 0
    var nonSpace = 0
    var inWord = false
    for (c in text) {
        if (c == '\n') lines++
        if (c.isWhitespace()) {
            inWord = false
        } else {
            nonSpace++
            if (!inWord) words++
            inWord = true
        }
    }

    val caret = selection.end.coerceIn(0, text.length)
    val caretLineStart = text.lastIndexOf('\n', caret - 1) + 1
    val caretLine = text.subSequence(0, caret).count { it == '\n' } + 1

    val selMin = selection.min.coerceIn(0, text.length)
    val selMax = selection.max.coerceIn(0, text.length)
    val selectedLines =
        if (selMin == selMax) 0 else text.subSequence(selMin, selMax).count { it == '\n' } + 1

    return EditorStats(
        lines = lines,
        words = words,
        characters = text.length,
        charactersNoSpaces = nonSpace,
        caretLine = caretLine,
        caretColumn = caret - caretLineStart + 1,
        selectedCharacters = selMax - selMin,
        selectedLines = selectedLines
    )
}

@Composable
fun StatusBar(state: TextFieldState, modifier: Modifier = Modifier) {
    val stats by remember(state) {
        derivedStateOf { computeEditorStats(state.text, state.selection) }
    }
    val colors = MaterialTheme.colorScheme

    Row(
        modifier
            .fillMaxWidth()
            .height(26.dp)
            .background(colors.surfaceVariant)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusItem("Ln ${stats.caretLine}, Col ${stats.caretColumn}")
        if (stats.selectedCharacters > 0) {
            StatusItem(
                buildString {
                    append("${stats.selectedCharacters} selected")
                    if (stats.selectedLines > 1) append(" (${stats.selectedLines} lines)")
                }
            )
        }
        Spacer(Modifier.weight(1f))
        StatusItem("Lines: ${stats.lines}")
        StatusDivider()
        StatusItem("Words: ${stats.words}")
        StatusDivider()
        StatusItem("Chars: ${stats.characters}")
        StatusDivider()
        StatusItem("Chars (no spaces): ${stats.charactersNoSpaces}")
        StatusDivider()
        StatusItem("UTF-8")
    }
}

@Composable
private fun StatusItem(text: String) {
    Text(
        text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
    )
}

@Composable
private fun StatusDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(14.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
