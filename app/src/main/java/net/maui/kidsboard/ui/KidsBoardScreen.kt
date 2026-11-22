package net.maui.kidsboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import net.maui.kidsboard.ui.components.ColorPickerDialog
import net.maui.kidsboard.ui.components.DrawingCanvas
import net.maui.kidsboard.viewmodel.DrawingViewModel

@Composable
fun KidsBoardScreen(
    viewModel: DrawingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fullscreen Drawing Canvas
        DrawingCanvas(
            strokes = uiState.strokes,
            currentStroke = uiState.currentStroke,
            onStartStroke = viewModel::startStroke,
            onAddPoint = viewModel::addPointToStroke,
            onFinishStroke = viewModel::finishStroke
        )

        // Floating Control Bar with Edge-to-Edge safe insets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Color Picker Swatch Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isEraser) Color.White else uiState.selectedColor)
                            .border(BorderStroke(2.dp, Color(0x33000000)), CircleShape)
                            .clickable { viewModel.openColorPicker() },
                        contentAlignment = Alignment.Center
                    ) {
                        val iconColor = if (uiState.isEraser || uiState.selectedColor == Color.White || uiState.selectedColor == Color(0xFFFFFF00) || uiState.selectedColor == Color(0xFF00FFFF)) {
                            Color.Black
                        } else {
                            Color.White
                        }
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Choose Color",
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Undo Button
                    IconButton(
                        onClick = viewModel::undo,
                        enabled = uiState.canUndo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo"
                        )
                    }

                    // Clear Canvas Button
                    IconButton(
                        onClick = viewModel::clearCanvas,
                        enabled = uiState.strokes.isNotEmpty() || uiState.currentStroke != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Canvas"
                        )
                    }
                }
            }
        }

        // Color Picker Dialog
        if (uiState.isColorPickerOpen) {
            ColorPickerDialog(
                selectedColor = uiState.selectedColor,
                onColorSelected = viewModel::selectColor,
                onDismiss = viewModel::closeColorPicker
            )
        }
    }
}
