package net.maui.kidsboard.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StampSizeTest {

    @Test
    fun testStampSizesAscending() {
        val sizes = StampSize.allSizes
        assertEquals(4, sizes.size)
        assertEquals(StampSize.Small, sizes[0])
        assertEquals(StampSize.Medium, sizes[1])
        assertEquals(StampSize.Large, sizes[2])
        assertEquals(StampSize.Jumbo, sizes[3])

        assertTrue(sizes[0].size < sizes[1].size)
        assertTrue(sizes[1].size < sizes[2].size)
        assertTrue(sizes[2].size < sizes[3].size)
    }

    @Test
    fun testDefaultSize() {
        assertEquals(StampSize.Medium, StampSize.DEFAULT)
    }

    @Test
    fun testFromSizeExact() {
        assertEquals(StampSize.Small, StampSize.fromSize(56f))
        assertEquals(StampSize.Medium, StampSize.fromSize(90f))
        assertEquals(StampSize.Large, StampSize.fromSize(136f))
        assertEquals(StampSize.Jumbo, StampSize.fromSize(190f))
    }

    @Test
    fun testFromSizeClosest() {
        assertEquals(StampSize.Small, StampSize.fromSize(30f))
        assertEquals(StampSize.Small, StampSize.fromSize(70f))
        assertEquals(StampSize.Medium, StampSize.fromSize(85f))
        assertEquals(StampSize.Large, StampSize.fromSize(130f))
        assertEquals(StampSize.Jumbo, StampSize.fromSize(250f))
    }
}
