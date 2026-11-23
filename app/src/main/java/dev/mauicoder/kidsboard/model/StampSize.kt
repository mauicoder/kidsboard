package dev.mauicoder.kidsboard.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Preset sizes for stickers/stamps placed on the canvas.
 */
enum class StampSize(
    val label: String,
    val shortLabel: String,
    val size: Float,
    val previewSizeDp: Dp
) {
    Small(label = "Small", shortLabel = "S", size = 56f, previewSizeDp = 18.dp),
    Medium(label = "Medium", shortLabel = "M", size = 90f, previewSizeDp = 26.dp),
    Large(label = "Large", shortLabel = "L", size = 136f, previewSizeDp = 34.dp),
    Jumbo(label = "Jumbo", shortLabel = "XL", size = 190f, previewSizeDp = 42.dp);

    companion object {
        val DEFAULT = Medium
        val allSizes: List<StampSize> = entries

        /**
         * Resolves the closest StampSize to a given float size.
         */
        fun fromSize(size: Float): StampSize {
            return entries.minByOrNull { kotlin.math.abs(it.size - size) } ?: DEFAULT
        }
    }
}
