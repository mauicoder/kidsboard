package net.maui.kidsboard

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.PaletteColor
import net.maui.kidsboard.viewmodel.DrawingUiState
import net.maui.kidsboard.viewmodel.DrawingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DrawingViewModelTest {

    private lateinit var viewModel: DrawingViewModel

    @Before
    fun setUp() {
        viewModel = DrawingViewModel()
    }

    @Test
    fun testDefaultUiState() {
        val state = viewModel.uiState.value
        assertEquals(ColorPalette.Black, state.selectedColor)
        assertEquals(DrawingUiState.DEFAULT_STROKE_WIDTH, state.strokeWidth)
        assertFalse(state.isEraser)
        assertFalse(state.isColorPickerOpen)
        assertFalse(state.canUndo)
        assertTrue(state.strokes.isEmpty())
        assertNull(state.currentStroke)
    }

    @Test
    fun testSelectColor() {
        val red = PaletteColor(ColorPalette.Red, "Red")
        viewModel.selectColor(red)

        val state = viewModel.uiState.value
        assertEquals(ColorPalette.Red, state.selectedColor)
        assertEquals(DrawingUiState.DEFAULT_STROKE_WIDTH, state.strokeWidth)
        assertFalse(state.isEraser)
        assertFalse(state.isColorPickerOpen)
    }

    @Test
    fun testSelectEraser() {
        val eraser = PaletteColor(ColorPalette.White, "Eraser", isEraser = true)
        viewModel.selectColor(eraser)

        val state = viewModel.uiState.value
        assertEquals(ColorPalette.White, state.selectedColor)
        assertEquals(DrawingUiState.ERASER_STROKE_WIDTH, state.strokeWidth)
        assertTrue(state.isEraser)
        assertFalse(state.isColorPickerOpen)
    }

    @Test
    fun testOpenAndCloseColorPicker() {
        viewModel.openColorPicker()
        assertTrue(viewModel.uiState.value.isColorPickerOpen)

        viewModel.closeColorPicker()
        assertFalse(viewModel.uiState.value.isColorPickerOpen)
    }

    @Test
    fun testStrokeLifecycle() {
        val startPoint = Offset(10f, 10f)
        viewModel.startStroke(startPoint)

        var state = viewModel.uiState.value
        assertNotNull(state.currentStroke)
        assertEquals(1, state.currentStroke?.points?.size)
        assertEquals(startPoint, state.currentStroke?.points?.first())

        val nextPoint = Offset(20f, 20f)
        viewModel.addPointToStroke(nextPoint)

        state = viewModel.uiState.value
        assertEquals(2, state.currentStroke?.points?.size)

        viewModel.finishStroke()

        state = viewModel.uiState.value
        assertNull(state.currentStroke)
        assertEquals(1, state.strokes.size)
        assertTrue(state.canUndo)
        assertEquals(2, state.strokes.first().points.size)
    }

    @Test
    fun testUndo() {
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        viewModel.startStroke(Offset(2f, 2f))
        viewModel.finishStroke()

        assertEquals(2, viewModel.uiState.value.strokes.size)

        viewModel.undo()
        assertEquals(1, viewModel.uiState.value.strokes.size)

        viewModel.undo()
        assertEquals(0, viewModel.uiState.value.strokes.size)
        assertFalse(viewModel.uiState.value.canUndo)

        // Extra undo when empty should not crash
        viewModel.undo()
        assertEquals(0, viewModel.uiState.value.strokes.size)
    }

    @Test
    fun testClearCanvas() {
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        viewModel.clearCanvas()
        assertTrue(viewModel.uiState.value.strokes.isEmpty())
        assertNull(viewModel.uiState.value.currentStroke)
        assertFalse(viewModel.uiState.value.canUndo)
    }
}
