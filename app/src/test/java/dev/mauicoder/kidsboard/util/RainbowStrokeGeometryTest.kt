package dev.mauicoder.kidsboard.util

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RainbowStrokeGeometryTest {

    @Test
    fun testEmptyOrSinglePointProducesNoSubdivisions() {
        var callCount = 0
        RainbowStrokeGeometry.forEachSubdivision(emptyList(), 10f) { _, _, _ ->
            callCount++
        }
        assertEquals(0, callCount)

        RainbowStrokeGeometry.forEachSubdivision(listOf(Offset(10f, 10f)), 10f) { _, _, _ ->
            callCount++
        }
        assertEquals(0, callCount)
    }

    @Test
    fun testTwoPointsProducesSmoothContinuousSubdivisions() {
        val points = listOf(Offset(0f, 0f), Offset(100f, 0f))
        val segments = mutableListOf<Triple<Offset, Offset, Float>>()

        RainbowStrokeGeometry.forEachSubdivision(points, strokeWidth = 14f) { p0, p1, dist ->
            segments.add(Triple(p0, p1, dist))
        }

        assertTrue("Should have multiple sub-segments along 100px line", segments.size > 10)

        // Check continuity: end of previous segment must match start of next
        for (i in 1 until segments.size) {
            val prevEnd = segments[i - 1].second
            val currStart = segments[i].first
            assertEquals(prevEnd.x, currStart.x, 0.001f)
            assertEquals(prevEnd.y, currStart.y, 0.001f)

            // Cumulative distance must be strictly increasing
            assertTrue(segments[i].third > segments[i - 1].third)
        }
    }

    @Test
    fun testMultiPointPathCoversAllSegments() {
        val points = listOf(
            Offset(0f, 0f),
            Offset(50f, 50f),
            Offset(100f, 0f),
            Offset(150f, 50f)
        )
        var totalSegments = 0
        var maxDistance = 0f

        RainbowStrokeGeometry.forEachSubdivision(points, strokeWidth = 20f) { _, _, dist ->
            totalSegments++
            maxDistance = dist
        }

        assertTrue(totalSegments > 20)
        assertTrue("Cumulative distance should be over 150px", maxDistance > 150f)
    }
}
