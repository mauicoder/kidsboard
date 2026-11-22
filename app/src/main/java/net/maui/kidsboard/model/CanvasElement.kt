package net.maui.kidsboard.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Common interface for all drawable elements on the canvas.
 */
sealed interface CanvasElement

/**
 * Represents a stroke drawn on the canvas.
 */
data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false,
    val isRainbow: Boolean = false,
    val clipRegionId: Int? = null
) : CanvasElement

/**
 * Represents a stamp/sticker placed on the canvas.
 */
data class DrawingStamp(
    val emoji: String,
    val position: Offset,
    val size: Float
) : CanvasElement
