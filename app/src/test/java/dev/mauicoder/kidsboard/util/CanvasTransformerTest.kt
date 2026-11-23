package dev.mauicoder.kidsboard.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import dev.mauicoder.kidsboard.model.DrawingStamp
import dev.mauicoder.kidsboard.model.DrawingStroke
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CanvasTransformerTest {

    @Test
    fun calculateBoundsReturnsNullForEmptyList() {
        val bounds = CanvasTransformer.calculateBounds(emptyList())
        assertNull(bounds)
    }

    @Test
    fun calculateBoundsCalculatesAccurateBounds() {
        val stroke = DrawingStroke(
            points = listOf(Offset(10f, 20f), Offset(50f, 80f)),
            color = Color.Black,
            strokeWidth = 5f
        )
        val stamp = DrawingStamp(
            position = Offset(100f, 150f),
            emoji = "⭐",
            size = 40f
        )

        val bounds = CanvasTransformer.calculateBounds(listOf(stroke, stamp))
        assertNotNull(bounds)
        assertEquals(10f, bounds!!.left, 0.001f)
        assertEquals(20f, bounds.top, 0.001f)
        assertEquals(100f, bounds.right, 0.001f)
        assertEquals(150f, bounds.bottom, 0.001f)
    }

    @Test
    fun transformOnSizeChangeEmptyCanvasReturnsNewSize() {
        val result = CanvasTransformer.transformOnSizeChange(
            elements = emptyList(),
            redoElements = emptyList(),
            activeStrokes = emptyMap(),
            oldSize = IntSize(500, 500),
            newSize = IntSize(800, 800),
            currentContentWidth = 500f,
            currentContentHeight = 500f,
            currentOffsetX = 0f,
            currentOffsetY = 0f
        )

        assertEquals(800f, result.contentWidth, 0.001f)
        assertEquals(800f, result.contentHeight, 0.001f)
        assertEquals(0f, result.contentOffsetX, 0.001f)
        assertEquals(0f, result.contentOffsetY, 0.001f)
        assertTrue(result.elements.isEmpty())
    }

    @Test
    fun transformOnSizeChangeScalesStrokesUniformly() {
        val stroke = DrawingStroke(
            points = listOf(Offset(100f, 100f), Offset(200f, 200f)),
            color = Color.Red,
            strokeWidth = 10f
        )
        // Canvas size doubles from 500x500 to 1000x1000
        val result = CanvasTransformer.transformOnSizeChange(
            elements = listOf(stroke),
            redoElements = emptyList(),
            activeStrokes = emptyMap(),
            oldSize = IntSize(500, 500),
            newSize = IntSize(1000, 1000),
            currentContentWidth = 500f,
            currentContentHeight = 500f,
            currentOffsetX = 0f,
            currentOffsetY = 0f
        )

        val transformedStroke = result.elements.first() as DrawingStroke
        // Scale should be 2.0
        assertEquals(200f, transformedStroke.points[0].x, 0.01f)
        assertEquals(200f, transformedStroke.points[0].y, 0.01f)
        assertEquals(400f, transformedStroke.points[1].x, 0.01f)
        assertEquals(400f, transformedStroke.points[1].y, 0.01f)
        assertEquals(20f, transformedStroke.strokeWidth, 0.01f)
    }

    @Test
    fun transformOnSizeChangeScalesStamps() {
        val stamp = DrawingStamp(
            position = Offset(50f, 50f),
            emoji = "🌈",
            size = 30f
        )
        val result = CanvasTransformer.transformOnSizeChange(
            elements = listOf(stamp),
            redoElements = emptyList(),
            activeStrokes = emptyMap(),
            oldSize = IntSize(100, 100),
            newSize = IntSize(200, 200),
            currentContentWidth = 100f,
            currentContentHeight = 100f,
            currentOffsetX = 0f,
            currentOffsetY = 0f
        )

        val transformedStamp = result.elements.first() as DrawingStamp
        assertEquals(100f, transformedStamp.position.x, 0.01f)
        assertEquals(100f, transformedStamp.position.y, 0.01f)
        assertEquals(60f, transformedStamp.size, 0.01f)
    }
}
