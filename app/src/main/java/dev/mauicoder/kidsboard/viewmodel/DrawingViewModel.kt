package dev.mauicoder.kidsboard.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import dev.mauicoder.kidsboard.data.SoundPreferences
import dev.mauicoder.kidsboard.model.BackgroundCategory
import dev.mauicoder.kidsboard.model.BrushSize
import dev.mauicoder.kidsboard.model.CanvasBackground
import dev.mauicoder.kidsboard.model.CanvasElement
import dev.mauicoder.kidsboard.model.ColorPalette
import dev.mauicoder.kidsboard.model.ColoringRegion
import dev.mauicoder.kidsboard.model.ColoringRegionManager
import dev.mauicoder.kidsboard.model.DrawingStamp
import dev.mauicoder.kidsboard.model.DrawingStroke
import dev.mauicoder.kidsboard.model.DrawingTool
import dev.mauicoder.kidsboard.model.PaletteColor
import dev.mauicoder.kidsboard.model.StampRepository
import dev.mauicoder.kidsboard.model.StampSize
import dev.mauicoder.kidsboard.util.CanvasTransformer
import dev.mauicoder.kidsboard.util.DrawingExporter

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
    val selectedStampSize: StampSize = StampSize.DEFAULT,
    val stampSize: Float = StampSize.DEFAULT.size,
    val isEraser: Boolean = false,
    val isRainbow: Boolean = false,
    val activeTool: DrawingTool = DrawingTool.BRUSH,
    val selectedStamp: String = StampRepository.DEFAULT_STAMP,
    val isColorPickerOpen: Boolean = false,
    val isBrushSizePickerOpen: Boolean = false,
    val isStampPickerOpen: Boolean = false,
    val isBackgroundPickerOpen: Boolean = false,
    val isClearConfirmOpen: Boolean = false,
    val isSaving: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isStayInsideLines: Boolean = false,
    val selectedBackground: CanvasBackground = CanvasBackground.PlainWhite,
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val soundPreferences: SoundPreferences? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DrawingUiState(isSoundEnabled = soundPreferences?.isSoundEnabled ?: true)
    )
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    private val _saveEvents = MutableSharedFlow<SaveEvent>()
    val saveEvents: SharedFlow<SaveEvent> = _saveEvents.asSharedFlow()

    fun toggleSound() {
        _uiState.update { state ->
            val newState = !state.isSoundEnabled
            soundPreferences?.isSoundEnabled = newState
            state.copy(isSoundEnabled = newState)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _uiState.update { state ->
            soundPreferences?.isSoundEnabled = enabled
            state.copy(isSoundEnabled = enabled)
        }
    }

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

            val result = CanvasTransformer.transformOnSizeChange(
                elements = state.elements,
                redoElements = state.redoElements,
                activeStrokes = state.activeStrokes,
                oldSize = oldSize,
                newSize = newSize,
                currentContentWidth = state.contentWidth,
                currentContentHeight = state.contentHeight,
                currentOffsetX = state.contentOffsetX,
                currentOffsetY = state.contentOffsetY
            )

            state.copy(
                elements = result.elements,
                redoElements = result.redoElements,
                activeStrokes = result.activeStrokes,
                canvasSize = newSize,
                contentWidth = result.contentWidth,
                contentHeight = result.contentHeight,
                contentOffsetX = result.contentOffsetX,
                contentOffsetY = result.contentOffsetY
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
                isRainbow = paletteColor.isRainbow,
                activeTool = DrawingTool.BRUSH,
                isColorPickerOpen = false
            )
        }
    }

    fun selectBrushSize(brushSize: BrushSize) {
        _uiState.update { state ->
            val matchingStampSize = StampSize.fromSize(brushSize.stampSize)
            state.copy(
                selectedBrushSize = brushSize,
                strokeWidth = if (state.isEraser) DrawingUiState.ERASER_STROKE_WIDTH else brushSize.strokeWidth,
                selectedStampSize = matchingStampSize,
                stampSize = brushSize.stampSize,
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

    fun selectStampSize(stampSize: StampSize) {
        _uiState.update { state ->
            state.copy(
                selectedStampSize = stampSize,
                stampSize = stampSize.size,
                activeTool = DrawingTool.STAMP
            )
        }
    }

    fun selectStampAndSize(emoji: String, stampSize: StampSize) {
        _uiState.update { state ->
            state.copy(
                selectedStamp = emoji,
                selectedStampSize = stampSize,
                stampSize = stampSize.size,
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
            size = state.stampSize
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

    fun openBackgroundPicker() {
        _uiState.update { it.copy(isBackgroundPickerOpen = true) }
    }

    fun closeBackgroundPicker() {
        _uiState.update { it.copy(isBackgroundPickerOpen = false) }
    }

    fun selectBackground(background: CanvasBackground) {
        val currentBg = _uiState.value.selectedBackground
        if (currentBg == background) {
            _uiState.update { it.copy(isBackgroundPickerOpen = false) }
            return
        }

        var newColor = _uiState.value.selectedColor
        val isRainbow = _uiState.value.isRainbow

        // Smart contrast handling
        if (background.isDark && !currentBg.isDark) {
            if (newColor == ColorPalette.Black && !isRainbow) {
                newColor = ColorPalette.White
            }
        } else if (!background.isDark && currentBg.isDark) {
            if (newColor == ColorPalette.White && !isRainbow) {
                newColor = ColorPalette.Black
            }
        }

        _uiState.update {
            it.copy(
                selectedBackground = background,
                selectedColor = newColor,
                isBackgroundPickerOpen = false
            )
        }
    }

    fun toggleStayInsideLines() {
        _uiState.update { it.copy(isStayInsideLines = !it.isStayInsideLines) }
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
        val width = if (state.contentWidth > 0f) state.contentWidth else state.canvasSize.width.toFloat()
        val height = if (state.contentHeight > 0f) state.contentHeight else state.canvasSize.height.toFloat()

        val clipRegionId = if (state.isStayInsideLines &&
            state.selectedBackground.category == BackgroundCategory.COLORING &&
            width > 0f && height > 0f
        ) {
            val normX = (point.x / width).coerceIn(0f, 1f)
            val normY = (point.y / height).coerceIn(0f, 1f)
            val region = ColoringRegionManager.findRegionAt(state.selectedBackground, normX, normY)
            region?.id ?: ColoringRegion.OUTSIDE_REGION_ID
        } else {
            null
        }

        val stroke = DrawingStroke(
            points = listOf(point),
            color = if (state.isEraser) state.selectedBackground.baseColor else state.selectedColor,
            strokeWidth = state.strokeWidth,
            isEraser = state.isEraser,
            isRainbow = state.isRainbow,
            clipRegionId = clipRegionId
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
        if (state.isSaving || (state.elements.isEmpty() && state.activeStrokes.isEmpty() && state.selectedBackground == CanvasBackground.PlainWhite)) return

        val width = if (state.canvasSize.width > 0) state.canvasSize.width else 1080
        val height = if (state.canvasSize.height > 0) state.canvasSize.height else 1920

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch(ioDispatcher) {
            val result = exporter.exportToGallery(
                context = context,
                elements = state.elements,
                width = width,
                height = height,
                background = state.selectedBackground
            )

            _uiState.update { it.copy(isSaving = false) }

            result.fold(
                onSuccess = { uri -> _saveEvents.emit(SaveEvent.Success(uri)) },
                onFailure = { error -> _saveEvents.emit(SaveEvent.Error(error.localizedMessage ?: "Failed to save")) }
            )
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DrawingViewModel(
                    soundPreferences = SoundPreferences(context.applicationContext)
                ) as T
            }
        }
    }
}
