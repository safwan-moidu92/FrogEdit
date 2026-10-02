package com.osm.frogedit.settings

import androidx.compose.ui.graphics.Color

/** User-adjustable editor preferences. */
data class EditorSettings(
    val showLineNumbers: Boolean = true,
    val showStatusBar: Boolean = true,
    /** Background of the text editor box; [Color.Unspecified] uses the theme default. */
    val editorBackground: Color = Color.Unspecified,
    /** Color of the entered text; [Color.Unspecified] uses the theme default. */
    val textColor: Color = Color.Unspecified
)
