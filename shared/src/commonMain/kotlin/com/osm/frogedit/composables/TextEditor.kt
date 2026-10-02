package com.osm.frogedit.composables

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EditorPadding = 12.dp
private val GutterHorizontalPadding = 8.dp

@Composable
fun TextEditor(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
    showLineNumbers: Boolean = true,
    backgroundColor: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified
) {
    val scrollState = rememberScrollState()
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        color = textColor.takeOrElse { MaterialTheme.colorScheme.onSurface }
    )

    // 1-based logical line numbers spanned by the cursor / selection
    val text = state.text
    val selection = state.selection
    val lineCount = text.count { it == '\n' } + 1
    val activeStartLine = text.subSequence(0, selection.min).count { it == '\n' } + 1
    val activeEndLine = activeStartLine + text.subSequence(selection.min, selection.max).count { it == '\n' }

    Box(
        modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .then(if (backgroundColor.isSpecified) Modifier.background(backgroundColor) else Modifier)
    ) {
        Row(Modifier.fillMaxSize()) {
            if (showLineNumbers) {
                LineNumberGutter(
                    lineCount = lineCount,
                    activeLines = activeStartLine..activeEndLine,
                    layoutResult = layoutResult,
                    scrollState = scrollState,
                    textStyle = textStyle,
                    topPadding = EditorPadding
                )
            }

            BasicTextField(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(EditorPadding),
                textStyle = textStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                lineLimits = TextFieldLineLimits.MultiLine(),
                scrollState = scrollState,
                onTextLayout = { getResult -> layoutResult = getResult() }
            )
        }

        // Desktop-only scrollbar (jvmMain)
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}

/**
 * Draws line numbers aligned with the text field's laid-out lines. Uses the text layout so that
 * soft-wrapped lines only get a number on their first visual row, and follows the editor's scroll.
 */
@Composable
private fun LineNumberGutter(
    lineCount: Int,
    activeLines: IntRange,
    layoutResult: TextLayoutResult?,
    scrollState: ScrollState,
    textStyle: TextStyle,
    topPadding: Dp
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val colors = MaterialTheme.colorScheme
    val numberStyle = textStyle.copy(color = colors.onSurfaceVariant)
    val activeNumberStyle = textStyle.copy(color = colors.primary, fontWeight = FontWeight.Bold)
    val activeLineBackground = colors.primary.copy(alpha = 0.08f)

    // Size the gutter to fit the widest line number
    val digits = lineCount.toString().length
    val gutterWidth = with(density) {
        textMeasurer.measure("9".repeat(digits), activeNumberStyle).size.width.toDp() +
            GutterHorizontalPadding * 2
    }

    Box(
        Modifier
            .width(gutterWidth)
            .fillMaxHeight()
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .clipToBounds()
            .drawBehind {
                val layout = layoutResult ?: return@drawBehind
                val text = layout.layoutInput.text
                val yOffset = topPadding.toPx() - scrollState.value
                val rightEdge = size.width - GutterHorizontalPadding.toPx()

                // Walk logical lines by their start offsets and map each to the visual row
                // containing it, rather than inferring line starts from visual rows (which can
                // report odd start offsets, e.g. when the field is narrower than a glyph).
                var logicalLine = 0
                var lineStart = 0
                while (lineStart <= text.length) {
                    logicalLine++
                    val visualLine = layout.getLineForOffset(lineStart)
                    val nextBreak = text.indexOf('\n', lineStart)
                    lineStart = if (nextBreak == -1) text.length + 1 else nextBreak + 1

                    val top = layout.getLineTop(visualLine) + yOffset
                    val bottom = layout.getLineBottom(visualLine) + yOffset
                    if (bottom < 0f || top > size.height) continue

                    val isActive = logicalLine in activeLines
                    if (isActive) {
                        drawRect(activeLineBackground, Offset(0f, top), Size(size.width, bottom - top))
                    }
                    val measured = textMeasurer.measure(
                        logicalLine.toString(),
                        if (isActive) activeNumberStyle else numberStyle
                    )
                    drawText(measured, topLeft = Offset(rightEdge - measured.size.width, top))
                }
            }
    )
}
