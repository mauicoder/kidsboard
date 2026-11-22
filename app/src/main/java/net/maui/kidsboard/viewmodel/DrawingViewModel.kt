package net.maui.kidsboard.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.maui.kidsboard.model.BrushSize
import net.maui.kidsboard.model.CanvasElement
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStamp
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.DrawingTool
import net.maui.kidsboard.model.PaletteColor
import net.maui.kidsboard.model.StampRepository
import net.maui.kidsboard.util.DrawingExporter

sealed interface SaveEvent {
    data class Success(val uri: Uri) : SaveEvent
    data class Error(val message: String) : SaveEvent
}

data class DrawingUiState(
    val elements: List<CanvasElement> = emptyList(),
    val redoElements: List<CanvasElement> = emptyList(),
    val activeStrokes: Map<Long, DrawingStroke> = emptyMap(),
    val selectedColor: Color = ColorPalette.Black,
    val selectedBrushSize: BrushSize = BrushSize.Medium,
    val strokeWidth: Float = BrushSize.Medium.strokeWidth,
    val isEraser: Boolean = false,
    val activeTool: DrawingTool = DrawingTool.BRUSH,
    val selectedStamp: String = StampRepository.DEFAULT_STAMP,
    val isColorPickerOpen: Boolean = false,
    val isBrushSizePickerOpen: Boolean = false,
    val isStampPickerOpen: Boolean = false,
    val isClearConfirmOpen: Boolean = false,
    val isSaving: Boolean = false,
    val canvasSize: IntSize = IntSize.Zero,
    val contentWidth: Float = 0f,
    val contentHeight: Float = 0f,
    val contentOffsetX: Float = 0f,
    val contentOffsetY: Float = 0f
) {
    val canUndo: Boolean get() = elements.isNotEmpty()
    val canRedo: Boolean get() = redoElements.isNotEmpty()

    // Backward-compatibility properties
    val currentStroke: DrawingStroke? get() = activeStrokes.values.firstOrNull()
    val strokes: List<DrawingStroke> get() = elements.filterIsInstance<DrawingStroke>()
    val redoStrokes: List<DrawingStroke> get() = redoElements.filterIsInstance<DrawingStroke>()

    companion object {
        const val ERASER_STROKE_WIDTH = 48f
    }
}

class DrawingViewModel(
    private val exporter: DrawingExporter = DrawingExporter(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    private val _saveEvents = MutableSharedFlow<SaveEvent>()
    val saveEvents: SharedFlow<SaveEvent> = _saveEvents.asSharedFlow()

    fun updateCanvasSize(newSize: IntSize) {
        if (newSize.width <= 0 || newSize.height <= 0) return

        _uiState.update { state ->
            val oldSize = state.canvasSize
            if (oldSize.width <= 0 || oldSize.height <= 0 || oldSize == newSize) {
                return@update state.copy(
                    canvasSize = newSize,
                    contentWidth = if (state.contentWidth <= 0f) newSize.width.toFloat() else state.contentWidth,
                    contentHeight = if (state.contentHeight <= 0f) newSize.height.toFloat() else state.contentHeight
                )
            }

            if (state.elements.isEmpty() && state.redoElements.isEmpty() && state.activeStrokes.isEmpty()) {
                return@update state.copy(
                    canvasSize = newSize,
                    contentWidth = newSize.width.toFloat(),
                    contentHeight = newSize.height.toFloat(),
                    contentOffsetX = 0f,
                    contentOffsetY = 0f
                )
            }

            val allElements = state.elements + state.redoElements + state.activeStrokes.values
            val elementsBounds = getElementsBounds(allElements)
            val baseWidth = if (state.contentWidth > 0f) state.contentWidth else oldSize.width.toFloat()
            val baseHeight = if (state.contentHeight > 0f) state.contentHeight else oldSize.height.toFloat()
            val baseLeft = state.contentOffsetX
            val baseTop = state.contentOffsetY
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

            val transformedElements = state.elements.map { it.transform() }
            val transformedRedo = state.redoElements.map { it.transform() }
            val transformedActive = state.activeStrokes.mapValues { (_, stroke) ->
                stroke.transform() as DrawingStroke
            }

            state.copy(
                elements = transformedElements,
                redoElements = transformedRedo,
                activeStrokes = transformedActive,
                canvasSize = newSize,
                contentWidth = newContentWidth,
                contentHeight = newContentHeight,
                contentOffsetX = newOffsetX,
                contentOffsetY = newOffsetY
            )
        }
    }

    fun selectTool(tool: DrawingTool) {
        _uiState.update { it.copy(activeTool = tool) }
    }

    fun selectColor(paletteColor: PaletteColor) {
        _uiState.update { state ->
            val strokeWidth = if (paletteColor.isEraser) {
                DrawingUiState.ERASER_STROKE_WIDTH
            } else {
                state.selectedBrushSize.strokeWidth
            }
            state.copy(
                selectedColor = paletteColor.color,
                strokeWidth = strokeWidth,
                isEraser = paletteColor.isEraser,
                activeTool = DrawingTool.BRUSH,
                isColorPickerOpen = false
            )
        }
    }

    fun selectBrushSize(brushSize: BrushSize) {
        _uiState.update { state ->
            state.copy(
                selectedBrushSize = brushSize,
                strokeWidth = if (state.isEraser) DrawingUiState.ERASER_STROKE_WIDTH else brushSize.strokeWidth,
                isBrushSizePickerOpen = false
            )
        }
    }

    fun selectStamp(emoji: String) {
        _uiState.update { state ->
            state.copy(
                selectedStamp = emoji,
                activeTool = DrawingTool.STAMP,
                isStampPickerOpen = false
            )
        }
    }

    fun addStamp(position: Offset) {
        val state = _uiState.value
        val stamp = DrawingStamp(
            emoji = state.selectedStamp,
            position = position,
            size = state.selectedBrushSize.stampSize
        )
        _uiState.update {
            it.copy(
                elements = it.elements + stamp,
                redoElements = emptyList()
            )
        }
    }

    fun openColorPicker() {
        _uiState.update { it.copy(isColorPickerOpen = true) }
    }

    fun closeColorPicker() {
        _uiState.update { it.copy(isColorPickerOpen = false) }
    }

    fun openBrushSizePicker() {
        _uiState.update { it.copy(isBrushSizePickerOpen = true) }
    }

    fun closeBrushSizePicker() {
        _uiState.update { it.copy(isBrushSizePickerOpen = false) }
    }

    fun openStampPicker() {
        _uiState.update { it.copy(isStampPickerOpen = true) }
    }

    fun closeStampPicker() {
        _uiState.update { it.copy(isStampPickerOpen = false) }
    }

    fun requestClearCanvas() {
        val state = _uiState.value
        if (state.elements.isNotEmpty() || state.activeStrokes.isNotEmpty()) {
            _uiState.update { it.copy(isClearConfirmOpen = true) }
        }
    }

    fun dismissClearConfirm() {
        _uiState.update { it.copy(isClearConfirmOpen = false) }
    }

    fun confirmClearCanvas() {
        _uiState.update { state ->
            state.copy(
                elements = emptyList(),
                redoElements = emptyList(),
                activeStrokes = emptyMap(),
                contentWidth = state.canvasSize.width.toFloat(),
                contentHeight = state.canvasSize.height.toFloat(),
                contentOffsetX = 0f,
                contentOffsetY = 0f,
                isClearConfirmOpen = false
            )
        }
    }

    fun startStroke(pointerId: Long, point: Offset) {
        val state = _uiState.value
        val stroke = DrawingStroke(
            points = listOf(point),
            color = if (state.isEraser) Color.White else state.selectedColor,
            strokeWidth = state.strokeWidth,
            isEraser = state.isEraser
        )
        _uiState.update { it.copy(activeStrokes = it.activeStrokes + (pointerId to stroke)) }
    }

    fun startStroke(point: Offset) {
        startStroke(0L, point)
    }

    fun addPointToStroke(pointerId: Long, point: Offset) {
        _uiState.update { state ->
            val current = state.activeStrokes[pointerId] ?: return@update state
            val updated = current.copy(points = current.points + point)
            state.copy(activeStrokes = state.activeStrokes + (pointerId to updated))
        }
    }

    fun addPointToStroke(point: Offset) {
        addPointToStroke(0L, point)
    }

    fun finishStroke(pointerId: Long) {
        _uiState.update { state ->
            val finished = state.activeStrokes[pointerId]
            val remaining = state.activeStrokes - pointerId
            if (finished != null && finished.points.isNotEmpty()) {
                state.copy(
                    elements = state.elements + finished,
                    redoElements = emptyList(), // Starting a new stroke clears redo history
                    activeStrokes = remaining
                )
            } else {
                state.copy(activeStrokes = remaining)
            }
        }
    }

    fun finishStroke() {
        finishStroke(0L)
    }

    fun finishAllStrokes() {
        _uiState.update { state ->
            if (state.activeStrokes.isEmpty()) return@update state
            val validFinished = state.activeStrokes.values.filter { it.points.isNotEmpty() }
            state.copy(
                elements = state.elements + validFinished,
                redoElements = if (validFinished.isNotEmpty()) emptyList() else state.redoElements,
                activeStrokes = emptyMap()
            )
        }
    }

    fun undo() {
        _uiState.update { state ->
            if (state.elements.isNotEmpty()) {
                val lastElement = state.elements.last()
                state.copy(
                    elements = state.elements.dropLast(1),
                    redoElements = state.redoElements + lastElement
                )
            } else {
                state
            }
        }
    }

    fun redo() {
        _uiState.update { state ->
            if (state.redoElements.isNotEmpty()) {
                val elementToRestore = state.redoElements.last()
                state.copy(
                    elements = state.elements + elementToRestore,
                    redoElements = state.redoElements.dropLast(1)
                )
            } else {
                state
            }
        }
    }

    fun saveDrawing(context: Context) {
        val state = _uiState.value
        if (state.isSaving || (state.elements.isEmpty() && state.activeStrokes.isEmpty())) return

        val width = if (state.canvasSize.width > 0) state.canvasSize.width else 1080
        val height = if (state.canvasSize.height > 0) state.canvasSize.height else 1920

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch(ioDispatcher) {
            val result = exporter.exportToGallery(
                context = context,
                elements = state.elements,
                width = width,
                height = height
            )

            _uiState.update { it.copy(isSaving = false) }

            result.fold(
                onSuccess = { uri -> _saveEvents.emit(SaveEvent.Success(uri)) },
                onFailure = { error -> _saveEvents.emit(SaveEvent.Error(error.localizedMessage ?: "Failed to save")) }
            )
        }
    }

    private data class ElementBounds(val left: Float, val top: Float, val right: Float, val bottom: Float)

    private fun getElementsBounds(elements: Collection<CanvasElement>): ElementBounds? {
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
}
