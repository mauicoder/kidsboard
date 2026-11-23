package dev.mauicoder.kidsboard.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import dev.mauicoder.kidsboard.model.CanvasElement
import dev.mauicoder.kidsboard.model.DrawingStamp
import dev.mauicoder.kidsboard.model.DrawingStroke

data class ElementBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class TransformResult(
    val elements: List<CanvasElement>,
    val redoElements: List<CanvasElement>,
    val activeStrokes: Map<Long, DrawingStroke>,
    val contentWidth: Float,
    val contentHeight: Float,
    val contentOffsetX: Float,
    val contentOffsetY: Float
)

object CanvasTransformer {

    fun calculateBounds(elements: Collection<CanvasElement>): ElementBounds? {
        if (elements.isEmpty()) return null
        var minX = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE
        var hasPoints = false

        for (element in elements) {
            when (element) {
                is DrawingStroke -> {
                    for (point in element.points) {
                        minX = minOf(minX, point.x)
                        maxX = maxOf(maxX, point.x)
                        minY = minOf(minY, point.y)
                        maxY = maxOf(maxY, point.y)
                        hasPoints = true
                    }
                }
                is DrawingStamp -> {
                    minX = minOf(minX, element.position.x)
                    maxX = maxOf(maxX, element.position.x)
                    minY = minOf(minY, element.position.y)
                    maxY = maxOf(maxY, element.position.y)
                    hasPoints = true
                }
            }
        }
        return if (hasPoints) ElementBounds(minX, minY, maxX, maxY) else null
    }

    fun transformOnSizeChange(
        elements: List<CanvasElement>,
        redoElements: List<CanvasElement>,
        activeStrokes: Map<Long, DrawingStroke>,
        oldSize: IntSize,
        newSize: IntSize,
        currentContentWidth: Float,
        currentContentHeight: Float,
        currentOffsetX: Float,
        currentOffsetY: Float
    ): TransformResult {
        if (elements.isEmpty() && redoElements.isEmpty() && activeStrokes.isEmpty()) {
            return TransformResult(
                elements = emptyList(),
                redoElements = emptyList(),
                activeStrokes = emptyMap(),
                contentWidth = newSize.width.toFloat(),
                contentHeight = newSize.height.toFloat(),
                contentOffsetX = 0f,
                contentOffsetY = 0f
            )
        }

        val allElements = elements + redoElements + activeStrokes.values
        val elementsBounds = calculateBounds(allElements)
        val baseWidth = if (currentContentWidth > 0f) currentContentWidth else oldSize.width.toFloat()
        val baseHeight = if (currentContentHeight > 0f) currentContentHeight else oldSize.height.toFloat()
        val baseLeft = currentOffsetX
        val baseTop = currentOffsetY
        val baseRight = baseLeft + baseWidth
        val baseBottom = baseTop + baseHeight

        val currentLeft = elementsBounds?.let { minOf(baseLeft, it.left) } ?: baseLeft
        val currentRight = elementsBounds?.let { maxOf(baseRight, it.right) } ?: baseRight
        val currentTop = elementsBounds?.let { minOf(baseTop, it.top) } ?: baseTop
        val currentBottom = elementsBounds?.let { maxOf(baseBottom, it.bottom) } ?: baseBottom

        val currentWidth = maxOf(1f, currentRight - currentLeft)
        val currentHeight = maxOf(1f, currentBottom - currentTop)

        val scaleX = newSize.width.toFloat() / currentWidth
        val scaleY = newSize.height.toFloat() / currentHeight
        val scale = minOf(scaleX, scaleY)

        val newContentWidth = currentWidth * scale
        val newContentHeight = currentHeight * scale
        val newOffsetX = (newSize.width - newContentWidth) / 2f
        val newOffsetY = (newSize.height - newContentHeight) / 2f

        fun CanvasElement.transform(): CanvasElement = when (this) {
            is DrawingStroke -> copy(
                points = points.map { p ->
                    Offset((p.x - currentLeft) * scale + newOffsetX, (p.y - currentTop) * scale + newOffsetY)
                },
                strokeWidth = strokeWidth * scale
            )
            is DrawingStamp -> copy(
                position = Offset((position.x - currentLeft) * scale + newOffsetX, (position.y - currentTop) * scale + newOffsetY),
                size = size * scale
            )
        }

        val transformedElements = elements.map { it.transform() }
        val transformedRedo = redoElements.map { it.transform() }
        val transformedActive = activeStrokes.mapValues { (_, stroke) ->
            stroke.transform() as DrawingStroke
        }

        return TransformResult(
            elements = transformedElements,
            redoElements = transformedRedo,
            activeStrokes = transformedActive,
            contentWidth = newContentWidth,
            contentHeight = newContentHeight,
            contentOffsetX = newOffsetX,
            contentOffsetY = newOffsetY
        )
    }
}
