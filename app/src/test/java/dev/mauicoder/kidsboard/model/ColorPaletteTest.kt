package dev.mauicoder.kidsboard.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ColorPaletteTest {

    @Test
    fun testRainbowContinuousSpectrumTransitions() {
        val cycle = ColorPalette.RAINBOW_CYCLE_LENGTH

        // 0 -> Red
        val red = ColorPalette.rainbowColorIntAt(0f, cycle)
        assertEquals(0xFFFF0000.toInt(), red)

        // 1/6 cycle -> Yellow
        val yellow = ColorPalette.rainbowColorIntAt(cycle / 6f, cycle)
        assertEquals(0xFFFFFF00.toInt(), yellow)

        // 2/6 cycle -> Green
        val green = ColorPalette.rainbowColorIntAt(cycle * 2f / 6f, cycle)
        assertEquals(0xFF00FF00.toInt(), green)

        // 3/6 cycle -> Cyan
        val cyan = ColorPalette.rainbowColorIntAt(cycle * 3f / 6f, cycle)
        assertEquals(0xFF00FFFF.toInt(), cyan)

        // 4/6 cycle -> Blue
        val blue = ColorPalette.rainbowColorIntAt(cycle * 4f / 6f, cycle)
        assertEquals(0xFF0000FF.toInt(), blue)

        // 5/6 cycle -> Magenta
        val magenta = ColorPalette.rainbowColorIntAt(cycle * 5f / 6f, cycle)
        assertEquals(0xFFFF00FF.toInt(), magenta)

        // Full cycle -> Red again
        val fullCycleRed = ColorPalette.rainbowColorIntAt(cycle, cycle)
        assertEquals(0xFFFF0000.toInt(), fullCycleRed)

        // Verify continuity: delta between consecutive 2px steps is small
        for (dist in 0..cycle.toInt() step 2) {
            val c1 = ColorPalette.rainbowColorIntAt(dist.toFloat(), cycle)
            val c2 = ColorPalette.rainbowColorIntAt((dist + 2).toFloat(), cycle)
            val r1 = (c1 shr 16) and 0xFF
            val g1 = (c1 shr 8) and 0xFF
            val b1 = c1 and 0xFF
            val r2 = (c2 shr 16) and 0xFF
            val g2 = (c2 shr 8) and 0xFF
            val b2 = c2 and 0xFF

            val maxDelta = maxOf(
                abs(r1 - r2),
                abs(g1 - g2),
                abs(b1 - b2)
            )
            // With 400px cycle and 2px step, maximum channel delta is ~4 per step (out of 255)
            assertTrue("Step at $dist should be smooth, max delta was $maxDelta", maxDelta <= 8)
        }
    }

    @Test
    fun testRainbowColorAtNegativeDistance() {
        val cycle = ColorPalette.RAINBOW_CYCLE_LENGTH
        val colorAtZero = ColorPalette.rainbowColorIntAt(0f, cycle)
        val colorAtNegativeCycle = ColorPalette.rainbowColorIntAt(-cycle, cycle)
        assertEquals(colorAtZero, colorAtNegativeCycle)
    }
}
