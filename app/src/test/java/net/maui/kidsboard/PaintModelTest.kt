package net.maui.kidsboard

import android.graphics.Color
import android.graphics.Paint
import net.maui.kidsboard.model.PaintModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class PaintModelTest {

    private lateinit var mockPaint: Paint
    private lateinit var paintModel: PaintModel

    @Before
    fun setUp() {
        mockPaint = mock(Paint::class.java)
        paintModel = PaintModel.createForTesting { mockPaint }
    }

    @Test
    fun testDefaultValues() {
        assertEquals(Color.BLACK, paintModel.color)
        assertEquals(PaintModel.DEFAULT_STROKE_WIDTH, paintModel.strokeWidth)
        assertNotNull(paintModel.paint)
    }

    @Test
    fun testSetColor() {
        paintModel.setColor(Color.RED)
        assertEquals(Color.RED, paintModel.color)
        assertEquals(PaintModel.DEFAULT_STROKE_WIDTH, paintModel.strokeWidth)
    }

    @Test
    fun testWhiteColorSetsEraserWidth() {
        paintModel.setColor(Color.WHITE)
        assertEquals(Color.WHITE, paintModel.color)
        assertEquals(PaintModel.ERASER_STROKE_WIDTH, paintModel.strokeWidth)
    }

    @Test
    fun testSetDefaultPaintResetsProperties() {
        paintModel.setColor(Color.WHITE)
        assertEquals(PaintModel.ERASER_STROKE_WIDTH, paintModel.strokeWidth)

        paintModel.setDefaultPaint()
        assertEquals(Color.BLACK, paintModel.color)
        assertEquals(PaintModel.DEFAULT_STROKE_WIDTH, paintModel.strokeWidth)
    }
}
