package com.osm.frogedit.files

/** Thrown when a file's bytes can't be shown as text in the editor. */
class UnsupportedFileException(message: String) : Exception(message)

enum class LineEnding(val separator: String) {
    LF("\n"),
    CRLF("\r\n"),
    CR("\r")
}

/**
 * Editor text plus the on-disk details needed to write it back the way it was read.
 * [text] always uses `\n` line breaks, which is what the editor's line counting expects.
 */
data class TextFileContent(
    val text: String,
    val lineEnding: LineEnding = LineEnding.LF,
    val hasBom: Boolean = false
)

private const val Utf8Bom = '﻿'
private val Utf8BomBytes = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())

/**
 * Decodes [bytes] as UTF-8 text for the editor: strips a leading BOM and normalizes CRLF / CR line
 * endings to `\n`, remembering both so [encodeTextFile] can restore them.
 *
 * @throws UnsupportedFileException if the bytes aren't valid UTF-8 or look like a binary file.
 */
fun decodeTextFile(bytes: ByteArray): TextFileContent {
    val raw = try {
        bytes.decodeToString(throwOnInvalidSequence = true)
    } catch (e: CharacterCodingException) {
        throw UnsupportedFileException("The file is not valid UTF-8 text.")
    }
    if ('\u0000' in raw) {
        throw UnsupportedFileException("The file appears to be binary, not text.")
    }
    val hasBom = raw.startsWith(Utf8Bom)
    val text = if (hasBom) raw.substring(1) else raw
    return TextFileContent(
        text = text.replace("\r\n", "\n").replace('\r', '\n'),
        lineEnding = detectLineEnding(text),
        hasBom = hasBom
    )
}

/** Encodes [content] as UTF-8 using its original line ending and BOM. */
fun encodeTextFile(content: TextFileContent): ByteArray {
    val text = if (content.lineEnding == LineEnding.LF) {
        content.text
    } else {
        content.text.replace("\n", content.lineEnding.separator)
    }
    val bytes = text.encodeToByteArray()
    return if (content.hasBom) Utf8BomBytes + bytes else bytes
}

/** The first line break in the file decides; files without one default to LF. */
private fun detectLineEnding(text: String): LineEnding {
    val index = text.indexOfFirst { it == '\n' || it == '\r' }
    return when {
        index == -1 || text[index] == '\n' -> LineEnding.LF
        index + 1 < text.length && text[index + 1] == '\n' -> LineEnding.CRLF
        else -> LineEnding.CR
    }
}
