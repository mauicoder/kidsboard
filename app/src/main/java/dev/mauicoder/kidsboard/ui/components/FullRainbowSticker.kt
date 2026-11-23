package dev.mauicoder.kidsboard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mauicoder.kidsboard.ui.components.sticker.FullRainbowSticker
import dev.mauicoder.kidsboard.ui.components.sticker.RainbowArchSticker

/**
 * Backward compatibility aliases delegating to StickerRegistry implementations.
 */
object FullRainbowStickerRenderer {
    val RainbowColors = dev.mauicoder.kidsboard.ui.components.sticker.RainbowStickerColors.ComposeColors
    val AndroidRainbowColors = dev.mauicoder.kidsboard.ui.components.sticker.RainbowStickerColors.AndroidColors

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
