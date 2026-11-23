package dev.mauicoder.kidsboard.ui.components.sticker

import android.graphics.Canvas as AndroidCanvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Common contract for custom vector stickers that render across both
 * interactive Compose Canvas and Android graphics Canvas (PNG export).
 */
interface CustomSticker {
    val id: String
    val contentDescription: String

    fun DrawScope.draw(center: Offset, size: Float)
    fun drawToCanvas(canvas: AndroidCanvas, cx: Float, cy: Float, size: Float)

    @Composable
    fun Icon(modifier: Modifier)
}
