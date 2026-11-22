package net.maui.kidsboard.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
