package net.maui.kidsboard.ui.components.sticker

import net.maui.kidsboard.model.StampRepository

/**
 * Registry mapping sticker IDs to [CustomSticker] implementations.
 * Enables zero-boilerplate addition of new custom stickers.
 */
object StickerRegistry {

    private val stickers: Map<String, CustomSticker> = listOf(
        FullRainbowSticker,
        RainbowArchSticker
    ).associateBy { it.id }

    fun find(id: String): CustomSticker? = stickers[id]

    fun isCustom(id: String): Boolean = stickers.containsKey(id)

    fun allCustomStickers(): List<CustomSticker> = stickers.values.toList()
}
