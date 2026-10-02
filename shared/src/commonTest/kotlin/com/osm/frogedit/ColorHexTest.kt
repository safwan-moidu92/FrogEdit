package com.osm.frogedit

import androidx.compose.ui.graphics.Color
import com.osm.frogedit.settings.parseHexColor
import com.osm.frogedit.settings.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ColorHexTest {

    @Test
    fun parsesRgbAndArgb() {
        assertEquals(Color(0xFF1B5E20), parseHexColor("#1B5E20"))
        assertEquals(Color(0xFF1B5E20), parseHexColor("1b5e20"))
        assertEquals(Color(0x801B5E20), parseHexColor("#801B5E20"))
    }

    @Test
    fun rejectsInvalid() {
        assertNull(parseHexColor(""))
        assertNull(parseHexColor("#12345"))
        assertNull(parseHexColor("#GGGGGG"))
    }

    @Test
    fun formatsAndRoundTrips() {
        assertEquals("#000000", Color.Black.toHexString())
        assertEquals("#1B5E20", Color(0xFF1B5E20).toHexString())
        assertEquals("#801B5E20", Color(0x801B5E20).toHexString())
        assertEquals(Color(0xFFFAF8F0), parseHexColor(Color(0xFFFAF8F0).toHexString()))
    }
}
