package com.osm.frogedit

import androidx.compose.ui.text.TextRange
import com.osm.frogedit.composables.computeEditorStats
import kotlin.test.Test
import kotlin.test.assertEquals

class EditorStatsTest {

    @Test
    fun emptyText() {
        val stats = computeEditorStats("", TextRange(0))
        assertEquals(1, stats.lines)
        assertEquals(0, stats.words)
        assertEquals(0, stats.characters)
        assertEquals(1, stats.caretLine)
        assertEquals(1, stats.caretColumn)
        assertEquals(0, stats.selectedCharacters)
    }

    @Test
    fun countsLinesWordsAndCharacters() {
        val text = "hello world\n  foo\tbar\n\n"
        val stats = computeEditorStats(text, TextRange(text.length))
        assertEquals(4, stats.lines)
        assertEquals(4, stats.words)
        assertEquals(text.length, stats.characters)
        assertEquals(16, stats.charactersNoSpaces)
        assertEquals(4, stats.caretLine)
        assertEquals(1, stats.caretColumn)
    }

    @Test
    fun caretAndSelection() {
        val text = "abc\ndefg\nhi"
        // Caret after "de" on line 2
        val caret = computeEditorStats(text, TextRange(6))
        assertEquals(2, caret.caretLine)
        assertEquals(3, caret.caretColumn)

        // Backwards selection from "f" to "b" spans 2 lines
        val sel = computeEditorStats(text, TextRange(6, 1))
        assertEquals(5, sel.selectedCharacters)
        assertEquals(2, sel.selectedLines)
        assertEquals(1, sel.caretLine)
        assertEquals(2, sel.caretColumn)
    }
}
