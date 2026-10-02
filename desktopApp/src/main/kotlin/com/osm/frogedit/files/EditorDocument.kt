package com.osm.frogedit.files

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

/** The text being edited plus the file it belongs to, if any. */
class EditorDocument {
    val textState = TextFieldState()

    /** The file this text was opened from or last saved to; null for a new, unsaved document. */
    var file by mutableStateOf<File?>(null)
        private set

    // On-disk format of the current file, restored on save
    private var lineEnding = LineEnding.LF
    private var hasBom = false

    // Text as of the last open/save, to detect unsaved edits
    private var savedText by mutableStateOf("")

    /** True when the text differs from what's on disk. Observable from composition. */
    val isModified: Boolean
        get() = !textState.text.contentEquals(savedText)

    val displayName: String
        get() = file?.name ?: "Untitled"

    /** Starts a new, empty, untitled document. */
    fun reset() {
        textState.loadText("")
        savedText = ""
        lineEnding = LineEnding.LF
        hasBom = false
        file = null
    }

    /** @throws UnsupportedFileException if the file can't be read as text. */
    suspend fun open(file: File) {
        val content = readTextFile(file)
        textState.loadText(content.text)
        savedText = content.text
        lineEnding = content.lineEnding
        hasBom = content.hasBom
        this.file = file
    }

    /**
     * Writes the text to [target] (the current file by default) in the current file's format.
     * @throws java.io.IOException if the file can't be written.
     */
    suspend fun save(target: File = checkNotNull(file) { "No file to save to" }) {
        // Snapshot first, so edits made while writing still count as unsaved
        val text = textState.text.toString()
        writeTextFile(target, TextFileContent(text, lineEnding, hasBom))
        savedText = text
        file = target
    }
}
