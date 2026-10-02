package com.osm.frogedit.files

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import java.awt.Component
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.IOException
import javax.swing.JOptionPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Files larger than this are refused; the editor isn't built for huge documents. */
private const val MaxFileSizeBytes = 10L * 1024 * 1024

/** Shows the native "Open" dialog and returns the chosen file, or null if cancelled. */
fun chooseFileToOpen(parent: Frame): File? {
    val dialog = FileDialog(parent, "Open", FileDialog.LOAD)
    dialog.isVisible = true // blocks until the user picks a file or cancels
    val name = dialog.file ?: return null
    return File(dialog.directory, name)
}

/**
 * Reads [file] off the UI thread and decodes it for the editor.
 *
 * @throws UnsupportedFileException if the file is too large, unreadable, or not text.
 */
suspend fun readTextFile(file: File): TextFileContent = withContext(Dispatchers.IO) {
    if (file.length() > MaxFileSizeBytes) {
        throw UnsupportedFileException("The file is larger than ${MaxFileSizeBytes / (1024 * 1024)} MB.")
    }
    val bytes = try {
        file.readBytes()
    } catch (e: IOException) {
        throw UnsupportedFileException("The file could not be read: ${e.message}")
    }
    decodeTextFile(bytes)
}

/**
 * Shows the native "Save" dialog, starting at [initial] if given, and returns the chosen file, or
 * null if cancelled. The native dialog asks before overwriting an existing file.
 */
fun chooseFileToSave(parent: Frame, initial: File?): File? {
    val dialog = FileDialog(parent, "Save As", FileDialog.SAVE)
    dialog.directory = initial?.parent
    dialog.file = initial?.name ?: "Untitled.txt"
    dialog.isVisible = true
    val name = dialog.file ?: return null
    return File(dialog.directory, name)
}

/** Writes [content] to [file] off the UI thread. */
suspend fun writeTextFile(file: File, content: TextFileContent) = withContext(Dispatchers.IO) {
    file.writeBytes(encodeTextFile(content))
}

enum class UnsavedChangesChoice { Save, Discard, Cancel }

/** Asks what to do with unsaved edits to [documentName] before they would be lost. */
fun askAboutUnsavedChanges(parent: Component, documentName: String): UnsavedChangesChoice {
    val options = arrayOf("Save", "Don't Save", "Cancel")
    val choice = JOptionPane.showOptionDialog(
        parent,
        "Do you want to save the changes to \"$documentName\"?",
        "Unsaved changes",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.WARNING_MESSAGE,
        null,
        options,
        options[0]
    )
    return when (choice) {
        0 -> UnsavedChangesChoice.Save
        1 -> UnsavedChangesChoice.Discard
        else -> UnsavedChangesChoice.Cancel // includes closing the prompt
    }
}

fun showFileError(parent: Component, action: String, file: File, reason: String?) {
    JOptionPane.showMessageDialog(
        parent,
        "Could not $action \"${file.name}\".\n${reason.orEmpty()}",
        "${action.replaceFirstChar { it.uppercase() }} failed",
        JOptionPane.ERROR_MESSAGE
    )
}

/** Replaces the editor's content with [text], puts the cursor at the start and resets undo. */
@OptIn(ExperimentalFoundationApi::class) // undoState
fun TextFieldState.loadText(text: String) {
    edit {
        replace(0, length, text)
        placeCursorBeforeCharAt(0)
    }
    undoState.clearHistory()
}
