package net.maui.kidsboard.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Common interface for all drawable elements on the canvas.
 */
sealed interface CanvasElement

/**
 * Represents a stroke drawn on the canvas.
 */
data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false,
    val isRainbow: Boolean = false
) : CanvasElement

/**
 * Represents a stamp/sticker placed on the canvas.
 */
data class DrawingStamp(
    val emoji: String,
    val position: Offset,
    val size: Float
) : CanvasElement

/**
 * Active drawing mode.
 */
enum class DrawingTool {
    BRUSH,
    STAMP
}

/**
 * Represents a selectable color in the palette.
 */
data class PaletteColor(
    val color: Color,
    val name: String,
    val isEraser: Boolean = false,
    val isRainbow: Boolean = false
)

/**
 * Preset brush thicknesses and stamp sizes for young artists.
 */
data class BrushSize(
    val label: String,
    val strokeWidth: Float,
    val previewSizeDp: Dp,
    val stampSize: Float = 84f
) {
    companion object {
        val Fine = BrushSize(label = "Fine", strokeWidth = 6f, previewSizeDp = 10.dp, stampSize = 56f)
        val Medium = BrushSize(label = "Medium", strokeWidth = 14f, previewSizeDp = 18.dp, stampSize = 90f)
        val Bold = BrushSize(label = "Bold", strokeWidth = 26f, previewSizeDp = 28.dp, stampSize = 136f)
        val Chunky = BrushSize(label = "Extra Chunky", strokeWidth = 45f, previewSizeDp = 38.dp, stampSize = 190f)

        val allSizes: List<BrushSize> = listOf(Fine, Medium, Bold, Chunky)
    }
}

object ColorPalette {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val DarkBlue = Color(0xFF0000A0)
    val Blue = Color(0xFF0000FF)
    val Cyan = Color(0xFF00FFFF)
    val Green = Color(0xFF00FF00)
    val Yellow = Color(0xFFFFFF00)
    val Orange = Color(0xFFFF6600)
    val Red = Color(0xFFFF0000)
    val Magenta = Color(0xFFFF00FF)
    val Brown = Color(0xFF835C3B)
    val Coffee = Color(0xFF6F4E37)

    val RainbowSpectrum: List<Color> = listOf(
        Color(0xFFFF0000), // Red
        Color(0xFFFF7F00), // Orange
        Color(0xFFFFFF00), // Yellow
        Color(0xFF00FF00), // Green
        Color(0xFF00FFFF), // Cyan
        Color(0xFF0000FF), // Blue
        Color(0xFF8B00FF)  // Violet
    )

    val Rainbow = PaletteColor(Color.Transparent, "Rainbow", isRainbow = true)

    val allColors: List<PaletteColor> = listOf(
        Rainbow,
        PaletteColor(Red, "Red"),
        PaletteColor(Orange, "Orange"),
        PaletteColor(Yellow, "Yellow"),
        PaletteColor(Green, "Green"),
        PaletteColor(Cyan, "Cyan"),
        PaletteColor(Blue, "Blue"),
        PaletteColor(DarkBlue, "Dark Blue"),
        PaletteColor(Magenta, "Magenta"),
        PaletteColor(Brown, "Brown"),
        PaletteColor(Coffee, "Coffee"),
        PaletteColor(Black, "Black"),
        PaletteColor(White, "Eraser", isEraser = true)
    )
}
