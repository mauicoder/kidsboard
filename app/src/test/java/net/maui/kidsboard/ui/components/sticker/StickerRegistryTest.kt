package net.maui.kidsboard.ui.components.sticker

import net.maui.kidsboard.model.StampRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StickerRegistryTest {

    @Test
    fun testRegisteredCustomStickers() {
        val fullRainbow = StickerRegistry.find(StampRepository.FULL_RAINBOW)
        assertNotNull(fullRainbow)
        assertEquals(StampRepository.FULL_RAINBOW, fullRainbow?.id)
        assertTrue(StickerRegistry.isCustom(StampRepository.FULL_RAINBOW))

        val archRainbow = StickerRegistry.find("🌈")
        assertNotNull(archRainbow)
        assertEquals("🌈", archRainbow?.id)
        assertTrue(StickerRegistry.isCustom("🌈"))
    }

    @Test
    fun testStandardEmojisAreNotCustom() {
        assertNull(StickerRegistry.find("⭐"))
        assertFalse(StickerRegistry.isCustom("⭐"))

        assertNull(StickerRegistry.find("🐶"))
        assertFalse(StickerRegistry.isCustom("🐶"))
    }
}
