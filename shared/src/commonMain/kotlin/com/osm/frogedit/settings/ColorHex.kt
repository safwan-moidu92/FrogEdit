package com.osm.frogedit.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Parses `#RRGGBB` or `#AARRGGBB` (the `#` is optional). Returns null if [text] isn't a valid color.
 */
fun parseHexColor(text: String): Color? {
    val hex = text.trim().removePrefix("#")
    if (hex.length != 6 && hex.length != 8) return null
    val value = hex.toLongOrNull(16) ?: return null
    return if (hex.length == 6) Color(0xFF000000 or value) else Color(value)
}

/** Formats as `#RRGGBB`, or `#AARRGGBB` when the color isn't fully opaque. */
fun Color.toHexString(): String {
    val argb = toArgb().toLong() and 0xFFFFFFFF
    return if (argb ushr 24 == 0xFFL) {
        "#" + (argb and 0xFFFFFF).toString(16).uppercase().padStart(6, '0')
    } else {
        "#" + argb.toString(16).uppercase().padStart(8, '0')
    }
}
