package com.osm.frogedit.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences

/**
 * Persists [EditorSettings] between launches using the platform's user preferences store
 * (~/.java/.userPrefs on Linux, the registry on Windows, a plist on macOS).
 */
object SettingsStore {
    private const val ShowLineNumbers = "showLineNumbers"
    private const val ShowStatusBar = "showStatusBar"
    private const val EditorBackground = "editorBackground"
    private const val TextColor = "textColor"

    private val prefs: Preferences = Preferences.userRoot().node("com/osm/frogedit")

    fun load(): EditorSettings {
        val defaults = EditorSettings()
        return EditorSettings(
            showLineNumbers = prefs.getBoolean(ShowLineNumbers, defaults.showLineNumbers),
            showStatusBar = prefs.getBoolean(ShowStatusBar, defaults.showStatusBar),
            editorBackground = getColor(EditorBackground),
            textColor = getColor(TextColor)
        )
    }

    fun save(settings: EditorSettings) {
        prefs.putBoolean(ShowLineNumbers, settings.showLineNumbers)
        prefs.putBoolean(ShowStatusBar, settings.showStatusBar)
        putColor(EditorBackground, settings.editorBackground)
        putColor(TextColor, settings.textColor)
        try {
            prefs.flush()
        } catch (e: BackingStoreException) {
            // Settings still apply for this session; they just won't survive a restart
            System.err.println("Failed to save settings: ${e.message}")
        }
    }

    /** Colors are stored as ARGB ints; a missing key means the theme default. */
    private fun getColor(key: String): Color =
        prefs.get(key, null)?.toIntOrNull()?.let { Color(it) } ?: Color.Unspecified

    private fun putColor(key: String, color: Color) {
        if (color.isSpecified) prefs.putInt(key, color.toArgb()) else prefs.remove(key)
    }
}
