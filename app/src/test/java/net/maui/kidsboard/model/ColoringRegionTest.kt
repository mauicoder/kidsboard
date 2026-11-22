package net.maui.kidsboard.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColoringRegionTest {

    @Test
    fun testPointInPolygon() {
        // Simple unit square from (0,0) to (1,1)
        val square = listOf(
            0f to 0f,
            1f to 0f,
            1f to 1f,
            0f to 1f
        )
        assertTrue(ColoringRegion.isPointInPolygon(0.5f, 0.5f, square))
        assertFalse(ColoringRegion.isPointInPolygon(1.5f, 0.5f, square))
        assertFalse(ColoringRegion.isPointInPolygon(-0.1f, 0.5f, square))
    }

    @Test
    fun testTeddyBearHitTestingAndPriorities() {
        val bg = CanvasBackground.CuteBear

        // Snout center should match Snout (priority 10), not Head (priority 1)
        val snoutRegion = ColoringRegionManager.findRegionAt(bg, 0.50f, 0.48f)
        assertNotNull(snoutRegion)
        assertEquals(105, snoutRegion?.id)
        assertEquals("Snout Muzzle", snoutRegion?.name)

        // Left inner ear should match Inner Ear (priority 10), not Outer Ear (priority 5)
        val innerEarRegion = ColoringRegionManager.findRegionAt(bg, 0.33f, 0.24f)
        assertNotNull(innerEarRegion)
        assertEquals(101, innerEarRegion?.id)
        assertEquals("Left Inner Ear", innerEarRegion?.name)

        // Head forehead (above snout, inside head circle)
        val headRegion = ColoringRegionManager.findRegionAt(bg, 0.50f, 0.30f)
        assertNotNull(headRegion)
        assertEquals(107, headRegion?.id)
        assertEquals("Head", headRegion?.name)

        // Outside bear (top left corner)
        val outside = ColoringRegionManager.findRegionAt(bg, 0.05f, 0.05f)
        assertNull(outside)
    }

    @Test
    fun testRocketShipHitTesting() {
        val bg = CanvasBackground.RocketShip

        // Porthole center should match inner window (priority 10)
        val window = ColoringRegionManager.findRegionAt(bg, 0.50f, 0.40f)
        assertNotNull(window)
        assertEquals(201, window?.id)
        assertEquals("Inner Porthole Window", window?.name)

        // Upper fuselage
        val fuselage = ColoringRegionManager.findRegionAt(bg, 0.50f, 0.20f)
        assertNotNull(fuselage)
        assertEquals(205, fuselage?.id)
        assertEquals("Fuselage", fuselage?.name)

        // Thruster flame
        val flame = ColoringRegionManager.findRegionAt(bg, 0.50f, 0.80f)
        assertNotNull(flame)
        assertEquals(207, flame?.id)
    }

    @Test
    fun testRaceCarWheelPriority() {
        val bg = CanvasBackground.RaceCar

        // Center of rear wheel -> rim (priority 15) over tire (priority 10) over car body (priority 1)
        val rim = ColoringRegionManager.findRegionAt(bg, 0.32f, 0.67f)
        assertNotNull(rim)
        assertEquals(403, rim?.id)
        assertEquals("Rear Wheel Rim", rim?.name)

        // Outer part of tire (r = 0.065 > rim 0.045, < tire 0.085)
        val tire = ColoringRegionManager.findRegionAt(bg, 0.32f, 0.605f)
        assertNotNull(tire)
        assertEquals(404, tire?.id)
        assertEquals("Rear Wheel Tire", tire?.name)
    }

    @Test
    fun testNonColoringBackgroundHasNoRegions() {
        val bg = CanvasBackground.PlainWhite
        assertTrue(ColoringRegionManager.getRegions(bg).isEmpty())
        assertNull(ColoringRegionManager.findRegionAt(bg, 0.5f, 0.5f))
    }

    @Test
    fun testGetRegionById() {
        val bg = CanvasBackground.FlowerButterfly
        val center = ColoringRegionManager.getRegionById(bg, 501)
        assertNotNull(center)
        assertEquals("Flower Center", center?.name)

        val nonExistent = ColoringRegionManager.getRegionById(bg, 9999)
        assertNull(nonExistent)
    }
}
