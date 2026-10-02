package com.osm.frogedit.files

import java.awt.Frame
import java.io.File
import java.io.IOException

/** The File-menu flows for [document]: dialogs, prompts and error reporting around open/save. */
class FileActions(private val document: EditorDocument, private val window: Frame) {

    suspend fun newDocument() {
        if (!confirmCanDiscard()) return
        document.reset()
    }

    suspend fun open() {
        if (!confirmCanDiscard()) return
        val file = chooseFileToOpen(window) ?: return
        try {
            document.open(file)
        } catch (e: UnsupportedFileException) {
            showFileError(window, "open", file, e.message)
        }
    }

    /** Saves to the current file, or asks for one if the document is new. Returns true if saved. */
    suspend fun save(): Boolean {
        val file = document.file ?: return saveAs()
        return saveTo(file)
    }

    /** Asks for a file and saves to it. Returns true if saved. */
    suspend fun saveAs(): Boolean {
        val file = chooseFileToSave(window, document.file) ?: return false
        return saveTo(file)
    }

    /**
     * Call before anything that would lose unsaved edits. Offers to save them; returns false if
     * the user cancelled (or saving failed), in which case the caller should stop.
     */
    suspend fun confirmCanDiscard(): Boolean {
        if (!document.isModified) return true
        return when (askAboutUnsavedChanges(window, document.displayName)) {
            UnsavedChangesChoice.Save -> save()
            UnsavedChangesChoice.Discard -> true
            UnsavedChangesChoice.Cancel -> false
        }
    }

    private suspend fun saveTo(file: File): Boolean =
        try {
            document.save(file)
            true
        } catch (e: IOException) {
            showFileError(window, "save", file, e.message)
            false
        }
}
