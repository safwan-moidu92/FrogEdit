package com.osm.frogedit

import com.osm.frogedit.files.LineEnding
import com.osm.frogedit.files.TextFileContent
import com.osm.frogedit.files.UnsupportedFileException
import com.osm.frogedit.files.decodeTextFile
import com.osm.frogedit.files.encodeTextFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TextFileCodecTest {

    private val bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())

    @Test
    fun decodesUtf8() {
        assertEquals(TextFileContent("héllo 🐸"), decodeTextFile("héllo 🐸".encodeToByteArray()))
        assertEquals(TextFileContent(""), decodeTextFile(ByteArray(0)))
    }

    @Test
    fun stripsBomAndNormalizesLineEndings() {
        val content = decodeTextFile(bom + "a\r\nb\rc\nd".encodeToByteArray())
        assertEquals("a\nb\nc\nd", content.text)
        assertEquals(LineEnding.CRLF, content.lineEnding)
        assertEquals(true, content.hasBom)
    }

    @Test
    fun detectsLineEnding() {
        assertEquals(LineEnding.LF, decodeTextFile("a\nb".encodeToByteArray()).lineEnding)
        assertEquals(LineEnding.CRLF, decodeTextFile("a\r\nb".encodeToByteArray()).lineEnding)
        assertEquals(LineEnding.CR, decodeTextFile("a\rb".encodeToByteArray()).lineEnding)
        assertEquals(LineEnding.CR, decodeTextFile("a\r".encodeToByteArray()).lineEnding)
        assertEquals(LineEnding.LF, decodeTextFile("no breaks".encodeToByteArray()).lineEnding)
    }

    @Test
    fun encodeRestoresOriginalFormat() {
        for (original in listOf("a\nb\n", "a\r\nb\r\n", "a\rb\r", "single line")) {
            for (bytes in listOf(original.encodeToByteArray(), bom + original.encodeToByteArray())) {
                assertContentEquals(bytes, encodeTextFile(decodeTextFile(bytes)))
            }
        }
    }

    @Test
    fun encodesNewTextAsPlainUtf8() {
        assertContentEquals("x\ny 🐸".encodeToByteArray(), encodeTextFile(TextFileContent("x\ny 🐸")))
    }

    @Test
    fun rejectsInvalidUtf8() {
        assertFailsWith<UnsupportedFileException> {
            decodeTextFile(byteArrayOf(0xC3.toByte(), 0x28))
        }
    }

    @Test
    fun rejectsBinary() {
        assertFailsWith<UnsupportedFileException> {
            decodeTextFile(byteArrayOf(0x50, 0x4B, 0x03, 0x04, 0x00, 0x00))
        }
    }
}
