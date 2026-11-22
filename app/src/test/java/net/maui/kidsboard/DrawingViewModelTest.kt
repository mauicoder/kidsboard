package net.maui.kidsboard

import android.content.Context
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.maui.kidsboard.model.BrushSize
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.PaletteColor
import net.maui.kidsboard.util.DrawingExporter
import net.maui.kidsboard.viewmodel.DrawingUiState
import net.maui.kidsboard.viewmodel.DrawingViewModel
import net.maui.kidsboard.viewmodel.SaveEvent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class DrawingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeExporter: FakeDrawingExporter
    private lateinit var viewModel: DrawingViewModel

    class FakeDrawingExporter : DrawingExporter() {
        var exportResult: Result<Uri> = Result.success(mock(Uri::class.java))
        override fun exportToGallery(
            context: Context,
            strokes: List<DrawingStroke>,
            width: Int,
            height: Int
        ): Result<Uri> = exportResult
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeExporter = FakeDrawingExporter()
        viewModel = DrawingViewModel(fakeExporter, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDefaultUiState() {
        val state = viewModel.uiState.value
        assertEquals(ColorPalette.Black, state.selectedColor)
        assertEquals(BrushSize.Medium, state.selectedBrushSize)
        assertEquals(BrushSize.Medium.strokeWidth, state.strokeWidth)
        assertFalse(state.isEraser)
        assertFalse(state.isColorPickerOpen)
        assertFalse(state.isBrushSizePickerOpen)
        assertFalse(state.isClearConfirmOpen)
        assertFalse(state.canUndo)
        assertFalse(state.canRedo)
        assertTrue(state.strokes.isEmpty())
        assertTrue(state.redoStrokes.isEmpty())
        assertNull(state.currentStroke)
    }

    @Test
    fun testSelectColor() {
        val red = PaletteColor(ColorPalette.Red, "Red")
        viewModel.selectColor(red)

        val state = viewModel.uiState.value
        assertEquals(ColorPalette.Red, state.selectedColor)
        assertEquals(BrushSize.Medium.strokeWidth, state.strokeWidth)
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
    fun testSelectBrushSize() {
        viewModel.selectBrushSize(BrushSize.Chunky)

        val state = viewModel.uiState.value
        assertEquals(BrushSize.Chunky, state.selectedBrushSize)
        assertEquals(BrushSize.Chunky.strokeWidth, state.strokeWidth)
        assertFalse(state.isBrushSizePickerOpen)
    }

    @Test
    fun testEraserRetainsEraserStrokeWidthWhenBrushSizeChanged() {
        viewModel.selectColor(PaletteColor(ColorPalette.White, "Eraser", isEraser = true))
        viewModel.selectBrushSize(BrushSize.Fine)

        val state = viewModel.uiState.value
        assertEquals(BrushSize.Fine, state.selectedBrushSize)
        assertEquals(DrawingUiState.ERASER_STROKE_WIDTH, state.strokeWidth)
        assertTrue(state.isEraser)
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
        assertFalse(state.canRedo)
        assertEquals(2, state.strokes.first().points.size)
    }

    @Test
    fun testUndoAndRedo() {
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        viewModel.startStroke(Offset(2f, 2f))
        viewModel.finishStroke()

        assertEquals(2, viewModel.uiState.value.strokes.size)
        assertFalse(viewModel.uiState.value.canRedo)

        // Undo 1
        viewModel.undo()
        assertEquals(1, viewModel.uiState.value.strokes.size)
        assertEquals(1, viewModel.uiState.value.redoStrokes.size)
        assertTrue(viewModel.uiState.value.canRedo)

        // Redo 1
        viewModel.redo()
        assertEquals(2, viewModel.uiState.value.strokes.size)
        assertEquals(0, viewModel.uiState.value.redoStrokes.size)
        assertFalse(viewModel.uiState.value.canRedo)
    }

    @Test
    fun testNewStrokeClearsRedoHistory() {
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        viewModel.undo()
        assertEquals(1, viewModel.uiState.value.redoStrokes.size)

        // Draw new stroke
        viewModel.startStroke(Offset(5f, 5f))
        viewModel.finishStroke()

        // Redo stack should now be cleared
        assertEquals(0, viewModel.uiState.value.redoStrokes.size)
        assertFalse(viewModel.uiState.value.canRedo)
    }

    @Test
    fun testClearCanvasConfirmationFlow() {
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        // Request clear
        viewModel.requestClearCanvas()
        assertTrue(viewModel.uiState.value.isClearConfirmOpen)

        // Dismiss clear
        viewModel.dismissClearConfirm()
        assertFalse(viewModel.uiState.value.isClearConfirmOpen)
        assertEquals(1, viewModel.uiState.value.strokes.size)

        // Confirm clear
        viewModel.requestClearCanvas()
        viewModel.confirmClearCanvas()
        assertFalse(viewModel.uiState.value.isClearConfirmOpen)
        assertTrue(viewModel.uiState.value.strokes.isEmpty())
        assertTrue(viewModel.uiState.value.redoStrokes.isEmpty())
    }

    @Test
    fun testSaveDrawingEmitsSuccess() = runTest {
        viewModel.startStroke(Offset(10f, 10f))
        viewModel.finishStroke()
        viewModel.updateCanvasSize(IntSize(800, 1200))

        val mockContext = mock(Context::class.java)
        var receivedEvent: SaveEvent? = null

        val job = launch {
            receivedEvent = viewModel.saveEvents.first()
        }

        viewModel.saveDrawing(mockContext)
        advanceUntilIdle()

        assertNotNull(receivedEvent)
        assertTrue(receivedEvent is SaveEvent.Success)
        job.cancel()
    }
}
