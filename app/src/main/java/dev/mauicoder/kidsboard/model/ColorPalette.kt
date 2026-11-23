package dev.mauicoder.kidsboard.model

import androidx.compose.ui.graphics.Color

/**
 * Represents a selectable color in the palette.
 */
data class PaletteColor(
    val color: Color,
    val name: String,
    val isEraser: Boolean = false,
    val isRainbow: Boolean = false
)

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

    const val RAINBOW_CYCLE_LENGTH = 400f

    /**
     * Computes the continuous rainbow spectrum color at a given distance along a stroke.
     * Transitions smoothly through 360° of HSV hue space without discrete jumps.
     */
    fun rainbowColorAt(distance: Float, cycleLength: Float = RAINBOW_CYCLE_LENGTH): Color {
        return Color(rainbowColorIntAt(distance, cycleLength))
    }

    /**
     * Computes the continuous rainbow ARGB 32-bit integer color at a given distance.
     * Fully allocation-free for smooth 60-120fps rendering and bitmap export.
     */
    fun rainbowColorIntAt(distance: Float, cycleLength: Float = RAINBOW_CYCLE_LENGTH): Int {
        val normalizedDist = ((distance % cycleLength) + cycleLength) % cycleLength
        val hue = (normalizedDist / cycleLength) * 360f
        return hsvToRgb(hue)
    }

    /**
     * Converts a hue angle (0..360°) to a 100% saturated and full brightness ARGB int.
     * Pure arithmetic calculation in CPU registers without array or object allocations.
     */
    fun hsvToRgb(hue: Float): Int {
        val normalizedHue = ((hue % 360f) + 360f) % 360f
        val hPrime = normalizedHue / 60f
        val x = 1f - kotlin.math.abs((hPrime % 2f) - 1f)
        val r: Float
        val g: Float
        val b: Float
        when (hPrime.toInt()) {
            0 -> { r = 1f; g = x; b = 0f }
            1 -> { r = x; g = 1f; b = 0f }
            2 -> { r = 0f; g = 1f; b = x }
            3 -> { r = 0f; g = x; b = 1f }
            4 -> { r = x; g = 0f; b = 1f }
            else -> { r = 1f; g = 0f; b = x }
        }
        val ri = (r * 255f + 0.5f).toInt()
        val gi = (g * 255f + 0.5f).toInt()
        val bi = (b * 255f + 0.5f).toInt()
        return (0xFF shl 24) or (ri shl 16) or (gi shl 8) or bi
    }

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
