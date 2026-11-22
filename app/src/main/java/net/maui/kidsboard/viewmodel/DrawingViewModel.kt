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
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.PaletteColor
import net.maui.kidsboard.util.DrawingExporter

sealed interface SaveEvent {
    data class Success(val uri: Uri) : SaveEvent
    data class Error(val message: String) : SaveEvent
}

data class DrawingUiState(
    val strokes: List<DrawingStroke> = emptyList(),
    val redoStrokes: List<DrawingStroke> = emptyList(),
    val currentStroke: DrawingStroke? = null,
    val selectedColor: Color = ColorPalette.Black,
    val selectedBrushSize: BrushSize = BrushSize.Medium,
    val strokeWidth: Float = BrushSize.Medium.strokeWidth,
    val isEraser: Boolean = false,
    val isColorPickerOpen: Boolean = false,
    val isBrushSizePickerOpen: Boolean = false,
    val isClearConfirmOpen: Boolean = false,
    val isSaving: Boolean = false,
    val canvasSize: IntSize = IntSize.Zero
) {
    val canUndo: Boolean get() = strokes.isNotEmpty()
    val canRedo: Boolean get() = redoStrokes.isNotEmpty()

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

    fun updateCanvasSize(size: IntSize) {
        if (size.width > 0 && size.height > 0) {
            _uiState.update { it.copy(canvasSize = size) }
        }
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

    fun requestClearCanvas() {
        if (_uiState.value.strokes.isNotEmpty() || _uiState.value.currentStroke != null) {
            _uiState.update { it.copy(isClearConfirmOpen = true) }
        }
    }

    fun dismissClearConfirm() {
        _uiState.update { it.copy(isClearConfirmOpen = false) }
    }

    fun confirmClearCanvas() {
        _uiState.update { state ->
            state.copy(
                strokes = emptyList(),
                redoStrokes = emptyList(),
                currentStroke = null,
                isClearConfirmOpen = false
            )
        }
    }

    fun startStroke(point: Offset) {
        val state = _uiState.value
        val stroke = DrawingStroke(
            points = listOf(point),
            color = if (state.isEraser) Color.White else state.selectedColor,
            strokeWidth = state.strokeWidth,
            isEraser = state.isEraser
        )
        _uiState.update { it.copy(currentStroke = stroke) }
    }

    fun addPointToStroke(point: Offset) {
        _uiState.update { state ->
            val current = state.currentStroke ?: return@update state
            state.copy(currentStroke = current.copy(points = current.points + point))
        }
    }

    fun finishStroke() {
        _uiState.update { state ->
            val finished = state.currentStroke
            if (finished != null && finished.points.isNotEmpty()) {
                state.copy(
                    strokes = state.strokes + finished,
                    redoStrokes = emptyList(), // Starting a new stroke clears redo history
                    currentStroke = null
                )
            } else {
                state.copy(currentStroke = null)
            }
        }
    }

    fun undo() {
        _uiState.update { state ->
            if (state.strokes.isNotEmpty()) {
                val lastStroke = state.strokes.last()
                state.copy(
                    strokes = state.strokes.dropLast(1),
                    redoStrokes = state.redoStrokes + lastStroke
                )
            } else {
                state
            }
        }
    }

    fun redo() {
        _uiState.update { state ->
            if (state.redoStrokes.isNotEmpty()) {
                val strokeToRestore = state.redoStrokes.last()
                state.copy(
                    strokes = state.strokes + strokeToRestore,
                    redoStrokes = state.redoStrokes.dropLast(1)
                )
            } else {
                state
            }
        }
    }

    fun saveDrawing(context: Context) {
        val state = _uiState.value
        if (state.isSaving || (state.strokes.isEmpty() && state.currentStroke == null)) return

        val width = if (state.canvasSize.width > 0) state.canvasSize.width else 1080
        val height = if (state.canvasSize.height > 0) state.canvasSize.height else 1920

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch(ioDispatcher) {
            val result = exporter.exportToGallery(
                context = context,
                strokes = state.strokes,
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
}
