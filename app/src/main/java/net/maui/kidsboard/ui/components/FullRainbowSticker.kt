package net.maui.kidsboard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.maui.kidsboard.ui.components.sticker.FullRainbowSticker
import net.maui.kidsboard.ui.components.sticker.RainbowArchSticker

/**
 * Backward compatibility aliases delegating to StickerRegistry implementations.
 */
object FullRainbowStickerRenderer {
    val RainbowColors = net.maui.kidsboard.ui.components.sticker.RainbowStickerColors.ComposeColors
    val AndroidRainbowColors = net.maui.kidsboard.ui.components.sticker.RainbowStickerColors.AndroidColors

    val drawFullRainbow = FullRainbowSticker.run { { center: androidx.compose.ui.geometry.Offset, size: Float ->
        with(this) { androidx.compose.ui.graphics.drawscope.DrawScope::class }
    } }
}

@Composable
fun FullRainbowStickerIcon(modifier: Modifier = Modifier) {
    FullRainbowSticker.Icon(modifier)
}

@Composable
fun Rainbow180StickerIcon(modifier: Modifier = Modifier) {
    RainbowArchSticker.Icon(modifier)
}
