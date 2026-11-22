package net.maui.kidsboard.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.PaletteColor

data class DrawingUiState(
    val strokes: List<DrawingStroke> = emptyList(),
    val currentStroke: DrawingStroke? = null,
    val selectedColor: Color = ColorPalette.Black,
    val strokeWidth: Float = DEFAULT_STROKE_WIDTH,
    val isEraser: Boolean = false,
    val isColorPickerOpen: Boolean = false
) {
    val canUndo: Boolean get() = strokes.isNotEmpty()

    companion object {
        const val DEFAULT_STROKE_WIDTH = 12f
        const val ERASER_STROKE_WIDTH = 45f
    }
}

class DrawingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    fun selectColor(paletteColor: PaletteColor) {
        _uiState.update { state ->
            val strokeWidth = if (paletteColor.isEraser) {
                DrawingUiState.ERASER_STROKE_WIDTH
            } else {
                DrawingUiState.DEFAULT_STROKE_WIDTH
            }
            state.copy(
                selectedColor = paletteColor.color,
                strokeWidth = strokeWidth,
                isEraser = paletteColor.isEraser,
                isColorPickerOpen = false
            )
        }
    }

    fun openColorPicker() {
        _uiState.update { it.copy(isColorPickerOpen = true) }
    }

    fun closeColorPicker() {
        _uiState.update { it.copy(isColorPickerOpen = false) }
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
                state.copy(strokes = state.strokes.dropLast(1))
            } else {
                state
            }
        }
    }

    fun clearCanvas() {
        _uiState.update { state ->
            state.copy(
                strokes = emptyList(),
                currentStroke = null
            )
        }
    }
}
