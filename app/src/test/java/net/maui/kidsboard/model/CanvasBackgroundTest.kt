package net.maui.kidsboard.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CanvasBackgroundTest {

    @Test
    fun testAllBackgroundsCountAndCategories() {
        assertEquals(4, CanvasBackground.PAPER_STYLES.size)
        assertEquals(5, CanvasBackground.COLORING_TEMPLATES.size)
        assertEquals(9, CanvasBackground.ALL.size)

        for (paper in CanvasBackground.PAPER_STYLES) {
            assertEquals(BackgroundCategory.PAPER, paper.category)
        }
        for (template in CanvasBackground.COLORING_TEMPLATES) {
            assertEquals(BackgroundCategory.COLORING, template.category)
        }
    }

    @Test
    fun testChalkboardIsDark() {
        assertTrue(CanvasBackground.Chalkboard.isDark)
        assertFalse(CanvasBackground.PlainWhite.isDark)
        assertFalse(CanvasBackground.GridPaper.isDark)
        assertFalse(CanvasBackground.LinedPaper.isDark)
        assertFalse(CanvasBackground.CuteBear.isDark)
    }

    @Test
    fun testFromId() {
        assertEquals(CanvasBackground.PlainWhite, CanvasBackground.fromId("white"))
        assertEquals(CanvasBackground.Chalkboard, CanvasBackground.fromId("chalkboard"))
        assertEquals(CanvasBackground.GridPaper, CanvasBackground.fromId("grid"))
        assertEquals(CanvasBackground.LinedPaper, CanvasBackground.fromId("lined"))
        assertEquals(CanvasBackground.CuteBear, CanvasBackground.fromId("cute_bear"))
        assertEquals(CanvasBackground.RocketShip, CanvasBackground.fromId("rocket_ship"))
        assertEquals(CanvasBackground.SunAndCloud, CanvasBackground.fromId("sun_cloud"))
        assertEquals(CanvasBackground.RaceCar, CanvasBackground.fromId("race_car"))
        assertEquals(CanvasBackground.FlowerButterfly, CanvasBackground.fromId("flower_butterfly"))
        // Fallback
        assertEquals(CanvasBackground.PlainWhite, CanvasBackground.fromId("unknown_id"))
    }

    @Test
    fun testColoringTemplatesNonEmptyAndNormalizedBounds() {
        for (template in CanvasBackground.COLORING_TEMPLATES) {
            val elements = ColoringOutlineTemplates.getTemplate(template)
            assertTrue("Template ${template.id} should not be empty", elements.isNotEmpty())

            for (element in elements) {
                when (element) {
                    is OutlineElement.Circle -> {
                        assertTrue("cx in bounds", element.cx in 0f..1f)
                        assertTrue("cy in bounds", element.cy in 0f..1f)
                        assertTrue("radius > 0", element.radius > 0f)
                    }
                    is OutlineElement.Oval -> {
                        assertTrue("cx in bounds", element.cx in 0f..1f)
                        assertTrue("cy in bounds", element.cy in 0f..1f)
                        assertTrue("rx > 0", element.rx > 0f)
                        assertTrue("ry > 0", element.ry > 0f)
                    }
                    is OutlineElement.Path -> {
                        assertTrue("Path actions non empty", element.actions.isNotEmpty())
                        for (action in element.actions) {
                            when (action) {
                                is PathAction.MoveTo -> {
                                    assertTrue("MoveTo x in bounds", action.x in 0f..1f)
                                    assertTrue("MoveTo y in bounds", action.y in 0f..1f)
                                }
                                is PathAction.LineTo -> {
                                    assertTrue("LineTo x in bounds", action.x in 0f..1f)
                                    assertTrue("LineTo y in bounds", action.y in 0f..1f)
                                }
                                is PathAction.QuadTo -> {
                                    assertTrue("QuadTo cx in bounds", action.cx in 0f..1f)
                                    assertTrue("QuadTo cy in bounds", action.cy in 0f..1f)
                                    assertTrue("QuadTo x in bounds", action.x in 0f..1f)
                                    assertTrue("QuadTo y in bounds", action.y in 0f..1f)
                                }
                                is PathAction.CubicTo -> {
                                    assertTrue("CubicTo c1x in bounds", action.c1x in 0f..1f)
                                    assertTrue("CubicTo c1y in bounds", action.c1y in 0f..1f)
                                    assertTrue("CubicTo c2x in bounds", action.c2x in 0f..1f)
                                    assertTrue("CubicTo c2y in bounds", action.c2y in 0f..1f)
                                    assertTrue("CubicTo x in bounds", action.x in 0f..1f)
                                    assertTrue("CubicTo y in bounds", action.y in 0f..1f)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
