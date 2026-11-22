package net.maui.kidsboard.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents a stroke drawn on the canvas.
 */
data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

/**
 * Represents a selectable color in the palette.
 */
data class PaletteColor(
    val color: Color,
    val name: String,
    val isEraser: Boolean = false
)

/**
 * Preset brush thicknesses for young artists.
 */
data class BrushSize(
    val label: String,
    val strokeWidth: Float,
    val previewSizeDp: Dp
) {
    companion object {
        val Fine = BrushSize(label = "Fine", strokeWidth = 6f, previewSizeDp = 10.dp)
        val Medium = BrushSize(label = "Medium", strokeWidth = 14f, previewSizeDp = 18.dp)
        val Bold = BrushSize(label = "Bold", strokeWidth = 26f, previewSizeDp = 28.dp)
        val Chunky = BrushSize(label = "Extra Chunky", strokeWidth = 45f, previewSizeDp = 38.dp)

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

    val allColors: List<PaletteColor> = listOf(
        PaletteColor(Black, "Black"),
        PaletteColor(DarkBlue, "Dark Blue"),
        PaletteColor(Blue, "Blue"),
        PaletteColor(Cyan, "Cyan"),
        PaletteColor(Green, "Green"),
        PaletteColor(Yellow, "Yellow"),
        PaletteColor(Orange, "Orange"),
        PaletteColor(Red, "Red"),
        PaletteColor(Magenta, "Magenta"),
        PaletteColor(Brown, "Brown"),
        PaletteColor(Coffee, "Coffee"),
        PaletteColor(White, "Eraser", isEraser = true)
    )
}
