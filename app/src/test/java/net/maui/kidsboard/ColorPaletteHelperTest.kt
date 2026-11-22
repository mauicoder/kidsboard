package net.maui.kidsboard

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorPaletteHelperTest {

    @Test
    fun testIsLightColorForWhiteAndYellow() {
        val white = 0xFFFFFFFF.toInt()
        val yellow = 0xFFFFFF00.toInt()
        val cyan = 0xFF00FFFF.toInt()

        assertTrue(ColorPaletteHelper.isLightColor(white))
        assertTrue(ColorPaletteHelper.isLightColor(yellow))
        assertTrue(ColorPaletteHelper.isLightColor(cyan))
    }

    @Test
    fun testIsDarkColorForBlackAndBlue() {
        val black = 0xFF000000.toInt()
        val darkBlue = 0xFF0000A0.toInt()
        val blue = 0xFF0000FF.toInt()
        val brown = 0xFF835C3B.toInt()
        val coffee = 0xFF6F4E37.toInt()

        assertFalse(ColorPaletteHelper.isLightColor(black))
        assertFalse(ColorPaletteHelper.isLightColor(darkBlue))
        assertFalse(ColorPaletteHelper.isLightColor(blue))
        assertFalse(ColorPaletteHelper.isLightColor(brown))
        assertFalse(ColorPaletteHelper.isLightColor(coffee))
    }

    @Test
    fun testContrastingColorReturnsBlackForLightBackgrounds() {
        val white = 0xFFFFFFFF.toInt()
        val yellow = 0xFFFFFF00.toInt()

        assertEquals(Color.BLACK, ColorPaletteHelper.getContrastingColor(white))
        assertEquals(Color.BLACK, ColorPaletteHelper.getContrastingColor(yellow))
    }

    @Test
    fun testContrastingColorReturnsWhiteForDarkBackgrounds() {
        val black = 0xFF000000.toInt()
        val darkBlue = 0xFF0000A0.toInt()

        assertEquals(Color.WHITE, ColorPaletteHelper.getContrastingColor(black))
        assertEquals(Color.WHITE, ColorPaletteHelper.getContrastingColor(darkBlue))
    }
}
