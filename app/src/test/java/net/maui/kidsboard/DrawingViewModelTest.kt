package net.maui.kidsboard

import android.content.Context
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
import net.maui.kidsboard.model.CanvasElement
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStamp
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.DrawingTool
import net.maui.kidsboard.model.PaletteColor
import net.maui.kidsboard.model.StampRepository
import net.maui.kidsboard.model.StampSize
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
            elements: List<CanvasElement>,
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
        assertEquals(DrawingTool.BRUSH, state.activeTool)
        assertEquals(StampRepository.DEFAULT_STAMP, state.selectedStamp)
        assertFalse(state.isEraser)
        assertFalse(state.isColorPickerOpen)
        assertFalse(state.isBrushSizePickerOpen)
        assertFalse(state.isStampPickerOpen)
        assertFalse(state.isClearConfirmOpen)
        assertFalse(state.canUndo)
        assertFalse(state.canRedo)
        assertTrue(state.elements.isEmpty())
        assertTrue(state.strokes.isEmpty())
        assertTrue(state.redoElements.isEmpty())
        assertNull(state.currentStroke)
    }

    @Test
    fun testSelectColor() {
        val red = PaletteColor(ColorPalette.Red, "Red")
        viewModel.selectColor(red)

        val state = viewModel.uiState.value
        assertEquals(ColorPalette.Red, state.selectedColor)
        assertEquals(BrushSize.Medium.strokeWidth, state.strokeWidth)
        assertEquals(DrawingTool.BRUSH, state.activeTool)
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
        assertEquals(DrawingTool.BRUSH, state.activeTool)
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
    fun testSelectStamp() {
        viewModel.selectStamp("🦖")

        val state = viewModel.uiState.value
        assertEquals("🦖", state.selectedStamp)
        assertEquals(DrawingTool.STAMP, state.activeTool)
        assertFalse(state.isStampPickerOpen)
    }

    @Test
    fun testAddStamp() {
        viewModel.selectStamp("🌈")
        viewModel.selectBrushSize(BrushSize.Bold)
        val position = Offset(150f, 250f)
        viewModel.addStamp(position)

        val state = viewModel.uiState.value
        assertEquals(1, state.elements.size)
        val stamp = state.elements.first() as DrawingStamp
        assertEquals("🌈", stamp.emoji)
        assertEquals(position, stamp.position)
        assertEquals(BrushSize.Bold.stampSize, stamp.size)
        assertTrue(state.canUndo)
        assertFalse(state.canRedo)
    }

    @Test
    fun testSelectStampSize() {
        viewModel.selectStampSize(StampSize.Jumbo)
        val state = viewModel.uiState.value
        assertEquals(StampSize.Jumbo, state.selectedStampSize)
        assertEquals(StampSize.Jumbo.size, state.stampSize)
        assertEquals(DrawingTool.STAMP, state.activeTool)
    }

    @Test
    fun testSelectStampAndSize() {
        viewModel.selectStampAndSize("🐶", StampSize.Small)
        val state = viewModel.uiState.value
        assertEquals("🐶", state.selectedStamp)
        assertEquals(StampSize.Small, state.selectedStampSize)
        assertEquals(StampSize.Small.size, state.stampSize)
        assertEquals(DrawingTool.STAMP, state.activeTool)

        val position = Offset(100f, 200f)
        viewModel.addStamp(position)
        val stamp = viewModel.uiState.value.elements.first() as DrawingStamp
        assertEquals("🐶", stamp.emoji)
        assertEquals(StampSize.Small.size, stamp.size)
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
        assertEquals(1, state.elements.size)
        assertEquals(1, state.strokes.size)
        assertTrue(state.canUndo)
        assertFalse(state.canRedo)
        assertEquals(2, state.strokes.first().points.size)
    }

    @Test
    fun testUndoAndRedoWithStrokesAndStamps() {
        // Draw stroke
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        // Place stamp
        viewModel.selectStamp("⭐")
        viewModel.addStamp(Offset(50f, 50f))

        assertEquals(2, viewModel.uiState.value.elements.size)
        assertTrue(viewModel.uiState.value.elements.last() is DrawingStamp)

        // Undo stamp
        viewModel.undo()
        assertEquals(1, viewModel.uiState.value.elements.size)
        assertTrue(viewModel.uiState.value.elements.first() is DrawingStroke)
        assertTrue(viewModel.uiState.value.canRedo)

        // Undo stroke
        viewModel.undo()
        assertTrue(viewModel.uiState.value.elements.isEmpty())

        // Redo stroke
        viewModel.redo()
        assertEquals(1, viewModel.uiState.value.elements.size)
        assertTrue(viewModel.uiState.value.elements.first() is DrawingStroke)

        // Redo stamp
        viewModel.redo()
        assertEquals(2, viewModel.uiState.value.elements.size)
        assertTrue(viewModel.uiState.value.elements.last() is DrawingStamp)
        assertFalse(viewModel.uiState.value.canRedo)
    }

    @Test
    fun testNewActionClearsRedoHistory() {
        viewModel.startStroke(Offset(1f, 1f))
        viewModel.finishStroke()

        viewModel.undo()
        assertEquals(1, viewModel.uiState.value.redoElements.size)

        // Add stamp
        viewModel.addStamp(Offset(20f, 20f))

        // Redo stack should now be cleared
        assertEquals(0, viewModel.uiState.value.redoElements.size)
        assertFalse(viewModel.uiState.value.canRedo)
    }

    @Test
    fun testClearCanvasConfirmationFlow() {
        viewModel.addStamp(Offset(10f, 10f))

        // Request clear
        viewModel.requestClearCanvas()
        assertTrue(viewModel.uiState.value.isClearConfirmOpen)

        // Dismiss clear
        viewModel.dismissClearConfirm()
        assertFalse(viewModel.uiState.value.isClearConfirmOpen)
        assertEquals(1, viewModel.uiState.value.elements.size)

        // Confirm clear
        viewModel.requestClearCanvas()
        viewModel.confirmClearCanvas()
        assertFalse(viewModel.uiState.value.isClearConfirmOpen)
        assertTrue(viewModel.uiState.value.elements.isEmpty())
        assertTrue(viewModel.uiState.value.redoElements.isEmpty())
    }

    @Test
    fun testSaveDrawingEmitsSuccess() = runTest {
        viewModel.selectStamp("🦄")
        viewModel.addStamp(Offset(100f, 100f))
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

    @Test
    fun testMultiTouchSimultaneousStrokes() {
        val finger1 = 1L
        val finger2 = 2L

        viewModel.startStroke(finger1, Offset(10f, 10f))
        viewModel.startStroke(finger2, Offset(100f, 100f))

        var state = viewModel.uiState.value
        assertEquals(2, state.activeStrokes.size)
        assertTrue(state.elements.isEmpty())

        viewModel.addPointToStroke(finger1, Offset(20f, 20f))
        viewModel.addPointToStroke(finger2, Offset(110f, 110f))

        state = viewModel.uiState.value
        assertEquals(2, state.activeStrokes[finger1]?.points?.size)
        assertEquals(2, state.activeStrokes[finger2]?.points?.size)

        // Lift finger 1
        viewModel.finishStroke(finger1)
        state = viewModel.uiState.value
        assertEquals(1, state.elements.size)
        assertEquals(1, state.activeStrokes.size)
        assertNotNull(state.activeStrokes[finger2])

        // Lift finger 2
        viewModel.finishStroke(finger2)
        state = viewModel.uiState.value
        assertEquals(2, state.elements.size)
        assertTrue(state.activeStrokes.isEmpty())
    }

    @Test
    fun testFinishAllStrokesCommitsActivePointers() {
        viewModel.startStroke(10L, Offset(5f, 5f))
        viewModel.startStroke(20L, Offset(50f, 50f))
        viewModel.startStroke(30L, Offset(100f, 100f))

        assertEquals(3, viewModel.uiState.value.activeStrokes.size)
        assertTrue(viewModel.uiState.value.elements.isEmpty())

        viewModel.finishAllStrokes()

        val state = viewModel.uiState.value
        assertTrue(state.activeStrokes.isEmpty())
        assertEquals(3, state.elements.size)
        assertTrue(state.canUndo)
    }

    @Test
    fun testClearCanvasClearsActiveStrokes() {
        viewModel.startStroke(1L, Offset(10f, 10f))
        viewModel.startStroke(2L, Offset(20f, 20f))

        viewModel.requestClearCanvas()
        assertTrue(viewModel.uiState.value.isClearConfirmOpen)

        viewModel.confirmClearCanvas()
        assertTrue(viewModel.uiState.value.activeStrokes.isEmpty())
        assertTrue(viewModel.uiState.value.elements.isEmpty())
    }

    @Test
    fun testCanvasRotationScalesStrokesAndStampsPreservingAspectFit() {
        // Initial Portrait size: 1000 x 2000
        viewModel.updateCanvasSize(IntSize(1000, 2000))

        // Draw a stroke centered at (500, 1000) with strokeWidth = 20f
        viewModel.selectBrushSize(BrushSize(label = "Test", strokeWidth = 20f, previewSizeDp = 10.dp, stampSize = 100f))
        viewModel.startStroke(Offset(500f, 1000f))
        viewModel.finishStroke()

        // Add a stamp centered at (500, 1000) with stampSize = 100f
        viewModel.selectStamp("⭐")
        viewModel.addStamp(Offset(500f, 1000f))

        assertEquals(2, viewModel.uiState.value.elements.size)

        // Rotate to Landscape: 2000 x 1000
        // scaleX = 2000/1000 = 2.0, scaleY = 1000/2000 = 0.5 -> scale = 0.5
        // offsetX = (2000 - 1000 * 0.5) / 2 = 750f
        // offsetY = (1000 - 2000 * 0.5) / 2 = 0f
        viewModel.updateCanvasSize(IntSize(2000, 1000))

        val state = viewModel.uiState.value
        assertEquals(IntSize(2000, 1000), state.canvasSize)

        val stroke = state.elements[0] as DrawingStroke
        val stamp = state.elements[1] as DrawingStamp

        // New coordinates: x = 500 * 0.5 + 750 = 1000f, y = 1000 * 0.5 + 0 = 500f (exact center of 2000x1000!)
        assertEquals(1000f, stroke.points[0].x, 0.01f)
        assertEquals(500f, stroke.points[0].y, 0.01f)
        assertEquals(10f, stroke.strokeWidth, 0.01f)

        assertEquals(1000f, stamp.position.x, 0.01f)
        assertEquals(500f, stamp.position.y, 0.01f)
        assertEquals(50f, stamp.size, 0.01f)

        // Rotate back to Portrait: 1000 x 2000
        viewModel.updateCanvasSize(IntSize(1000, 2000))
        val returnedState = viewModel.uiState.value
        val returnedStroke = returnedState.elements[0] as DrawingStroke
        val returnedStamp = returnedState.elements[1] as DrawingStamp

        assertEquals(500f, returnedStroke.points[0].x, 0.01f)
        assertEquals(1000f, returnedStroke.points[0].y, 0.01f)
        assertEquals(20f, returnedStroke.strokeWidth, 0.01f)

        assertEquals(500f, returnedStamp.position.x, 0.01f)
        assertEquals(1000f, returnedStamp.position.y, 0.01f)
        assertEquals(100f, returnedStamp.size, 0.01f)
    }

    @Test
    fun testCanvasRotationTransformsRedoStack() {
        // Initial Portrait size: 1000 x 2000
        viewModel.updateCanvasSize(IntSize(1000, 2000))

        viewModel.selectBrushSize(BrushSize(label = "Test", strokeWidth = 20f, previewSizeDp = 10.dp, stampSize = 100f))
        viewModel.startStroke(Offset(500f, 1000f))
        viewModel.finishStroke()

        // Undo into redo stack
        viewModel.undo()
        assertEquals(1, viewModel.uiState.value.redoElements.size)
        assertEquals(0, viewModel.uiState.value.elements.size)

        // Rotate to Landscape
        viewModel.updateCanvasSize(IntSize(2000, 1000))

        // Redo in Landscape
        viewModel.redo()
        val state = viewModel.uiState.value
        assertEquals(1, state.elements.size)
        val stroke = state.elements[0] as DrawingStroke

        // Must be scaled and centered in Landscape coordinate space
        assertEquals(1000f, stroke.points[0].x, 0.01f)
        assertEquals(500f, stroke.points[0].y, 0.01f)
        assertEquals(10f, stroke.strokeWidth, 0.01f)
    }

    @Test
    fun testSquareCanvasPreservesExactCoordinatesOnOrientationChange() {
        // Square canvas 1080 x 1080 (min(width, height) in both orientations)
        viewModel.updateCanvasSize(IntSize(1080, 1080))

        viewModel.selectBrushSize(BrushSize(label = "Medium", strokeWidth = 14f, previewSizeDp = 10.dp, stampSize = 80f))
        viewModel.startStroke(Offset(250f, 400f))
        viewModel.addPointToStroke(Offset(300f, 450f))
        viewModel.finishStroke()

        viewModel.selectStamp("🎨")
        viewModel.addStamp(Offset(600f, 700f))

        // Device rotates between Portrait and Landscape: canvas remains 1080 x 1080
        viewModel.updateCanvasSize(IntSize(1080, 1080))

        val state = viewModel.uiState.value
        assertEquals(IntSize(1080, 1080), state.canvasSize)

        val stroke = state.elements[0] as DrawingStroke
        val stamp = state.elements[1] as DrawingStamp

        assertEquals(250f, stroke.points[0].x, 0.01f)
        assertEquals(400f, stroke.points[0].y, 0.01f)
        assertEquals(300f, stroke.points[1].x, 0.01f)
        assertEquals(450f, stroke.points[1].y, 0.01f)
        assertEquals(14f, stroke.strokeWidth, 0.01f)

        assertEquals(600f, stamp.position.x, 0.01f)
        assertEquals(700f, stamp.position.y, 0.01f)
        assertEquals(80f, stamp.size, 0.01f)
    }

    @Test
    fun testSelectRainbowPaletteColorActivatesRainbowBrush() {
        viewModel.selectColor(ColorPalette.Rainbow)

        val state = viewModel.uiState.value
        assertTrue("Rainbow mode should be active", state.isRainbow)
        assertFalse(state.isEraser)
        assertEquals(DrawingTool.BRUSH, state.activeTool)
        assertFalse(state.isColorPickerOpen)
    }

    @Test
    fun testRainbowStrokeCreatedAndPersistsInElements() {
        viewModel.selectColor(ColorPalette.Rainbow)
        viewModel.startStroke(Offset(50f, 50f))

        val activeStroke = viewModel.uiState.value.currentStroke
        assertNotNull(activeStroke)
        assertTrue(activeStroke!!.isRainbow)

        viewModel.addPointToStroke(Offset(100f, 150f))
        viewModel.finishStroke()

        val state = viewModel.uiState.value
        assertEquals(1, state.elements.size)
        val stroke = state.elements.first() as DrawingStroke
        assertTrue(stroke.isRainbow)
        assertEquals(2, stroke.points.size)
    }

    @Test
    fun testSelectingStandardColorDisablesRainbowMode() {
        viewModel.selectColor(ColorPalette.Rainbow)
        assertTrue(viewModel.uiState.value.isRainbow)

        val blue = PaletteColor(ColorPalette.Blue, "Blue")
        viewModel.selectColor(blue)

        val state = viewModel.uiState.value
        assertFalse(state.isRainbow)
        assertEquals(ColorPalette.Blue, state.selectedColor)

        viewModel.startStroke(Offset(10f, 20f))
        val stroke = viewModel.uiState.value.currentStroke
        assertNotNull(stroke)
        assertFalse(stroke!!.isRainbow)
        assertEquals(ColorPalette.Blue, stroke.color)
    }

    @Test
    fun testSelectingEraserDisablesRainbowMode() {
        viewModel.selectColor(ColorPalette.Rainbow)
        assertTrue(viewModel.uiState.value.isRainbow)

        val eraser = PaletteColor(ColorPalette.White, "Eraser", isEraser = true)
        viewModel.selectColor(eraser)

        val state = viewModel.uiState.value
        assertFalse(state.isRainbow)
        assertTrue(state.isEraser)
        assertEquals(DrawingTool.BRUSH, state.activeTool)
    }

    @Test
    fun testMultiTouchRainbowStrokes() {
        viewModel.selectColor(ColorPalette.Rainbow)

        viewModel.startStroke(1L, Offset(10f, 10f))
        viewModel.startStroke(2L, Offset(200f, 200f))

        val activeStrokes = viewModel.uiState.value.activeStrokes
        assertEquals(2, activeStrokes.size)
        assertTrue(activeStrokes[1L]!!.isRainbow)
        assertTrue(activeStrokes[2L]!!.isRainbow)

        viewModel.finishAllStrokes()
        val elements = viewModel.uiState.value.elements
        assertEquals(2, elements.size)
        assertTrue((elements[0] as DrawingStroke).isRainbow)
        assertTrue((elements[1] as DrawingStroke).isRainbow)
    }

    @Test
    fun testSelectFullRainbowStamp() {
        viewModel.selectStamp(StampRepository.FULL_RAINBOW)

        val state = viewModel.uiState.value
        assertEquals(StampRepository.FULL_RAINBOW, state.selectedStamp)
        assertEquals(DrawingTool.STAMP, state.activeTool)
        assertFalse(state.isStampPickerOpen)
    }

    @Test
    fun testAddFullRainbowStamp() = runTest {
        viewModel.selectStamp(StampRepository.FULL_RAINBOW)
        viewModel.addStamp(Offset(200f, 300f))

        val state = viewModel.uiState.value
        assertEquals(1, state.elements.size)
        val stamp = state.elements.first() as DrawingStamp
        assertEquals(StampRepository.FULL_RAINBOW, stamp.emoji)
        assertEquals(200f, stamp.position.x, 0.01f)
        assertEquals(300f, stamp.position.y, 0.01f)

        // Verify save flow with full rainbow stamp
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

    @Test
    fun testRainbow180StampSelectionAndSave() = runTest {
        viewModel.selectStamp("🌈")

        val state = viewModel.uiState.value
        assertEquals("🌈", state.selectedStamp)
        assertEquals(DrawingTool.STAMP, state.activeTool)

        viewModel.addStamp(Offset(150f, 250f))

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.elements.size)
        val stamp = updatedState.elements.first() as DrawingStamp
        assertEquals("🌈", stamp.emoji)
        assertEquals(150f, stamp.position.x, 0.01f)
        assertEquals(250f, stamp.position.y, 0.01f)

        // Verify save flow with 180° rainbow stamp
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
