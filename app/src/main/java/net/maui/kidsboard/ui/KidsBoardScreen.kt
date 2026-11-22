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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import net.maui.kidsboard.model.DrawingTool
import net.maui.kidsboard.ui.components.BrushSizePickerDialog
import net.maui.kidsboard.ui.components.ClearConfirmDialog
import net.maui.kidsboard.ui.components.ColorPickerDialog
import net.maui.kidsboard.ui.components.DrawingCanvas
import net.maui.kidsboard.ui.components.StampPickerDialog
import net.maui.kidsboard.util.DrawingExporter
import net.maui.kidsboard.viewmodel.DrawingViewModel
import net.maui.kidsboard.viewmodel.SaveEvent

@Composable
fun KidsBoardScreen(
    viewModel: DrawingViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.saveEvents.collectLatest { event ->
            when (event) {
                is SaveEvent.Success -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Drawing saved to Gallery! 🎨",
                        actionLabel = "Share",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        DrawingExporter.shareDrawing(context, event.uri)
                    }
                }
                is SaveEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = "Could not save drawing: ${event.message}",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            // Fullscreen Canvas
            DrawingCanvas(
                elements = uiState.elements,
                currentStroke = uiState.currentStroke,
                activeTool = uiState.activeTool,
                onStartStroke = viewModel::startStroke,
                onAddPoint = viewModel::addPointToStroke,
                onFinishStroke = viewModel::finishStroke,
                onAddStamp = viewModel::addStamp,
                onSizeChanged = viewModel::updateCanvasSize
            )

            // Split Top Floating Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.safeDrawing.asPaddingValues())
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left Pill: Drawing Tools (Color + Brush Size + Stamps)
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color Swatch Button (Brush Mode)
                        val displayColor = if (uiState.isEraser) Color.White else uiState.selectedColor
                        val isBrushActive = uiState.activeTool == DrawingTool.BRUSH
                        val brushBorder = if (isBrushActive) {
                            BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.5.dp, Color(0x33000000))
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(displayColor)
                                .border(brushBorder, CircleShape)
                                .clickable {
                                    if (!isBrushActive) {
                                        viewModel.selectTool(DrawingTool.BRUSH)
                                    } else {
                                        viewModel.openColorPicker()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val iconColor = if (uiState.isEraser || displayColor == Color.White || displayColor == Color(0xFFFFFF00) || displayColor == Color(0xFF00FFFF)) {
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

                        // Brush / Stamp Size Button
                        IconButton(
                            onClick = viewModel::openBrushSizePicker
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Brush,
                                    contentDescription = "Brush & Stamp Size",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                // Active brush size dot indicator
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(displayColor)
                                        .border(BorderStroke(1.dp, Color(0x33000000)), CircleShape)
                                )
                            }
                        }

                        // Stamp Tool Button
                        val isStampActive = uiState.activeTool == DrawingTool.STAMP
                        val stampBackground = if (isStampActive) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        }
                        val stampBorder = if (isStampActive) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(stampBackground)
                                .border(stampBorder, CircleShape)
                                .clickable {
                                    if (isStampActive) {
                                        viewModel.openStampPicker()
                                    } else {
                                        viewModel.selectTool(DrawingTool.STAMP)
                                        viewModel.openStampPicker()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = uiState.selectedStamp,
                                fontSize = 22.sp
                            )
                        }
                    }
                }

                // Right Pill: Canvas Actions (Undo, Redo, Clear, Save/Share)
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                        // Redo Button
                        IconButton(
                            onClick = viewModel::redo,
                            enabled = uiState.canRedo
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Redo,
                                contentDescription = "Redo"
                            )
                        }

                        // Clear Canvas Button
                        IconButton(
                            onClick = viewModel::requestClearCanvas,
                            enabled = uiState.elements.isNotEmpty() || uiState.currentStroke != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Canvas"
                            )
                        }

                        // Save & Share Button
                        IconButton(
                            onClick = { viewModel.saveDrawing(context) },
                            enabled = !uiState.isSaving && (uiState.elements.isNotEmpty() || uiState.currentStroke != null)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Save and Share Drawing"
                                )
                            }
                        }
                    }
                }
            }

            // Dialogs
            if (uiState.isColorPickerOpen) {
                ColorPickerDialog(
                    selectedColor = uiState.selectedColor,
                    onColorSelected = viewModel::selectColor,
                    onDismiss = viewModel::closeColorPicker
                )
            }

            if (uiState.isBrushSizePickerOpen) {
                BrushSizePickerDialog(
                    selectedBrushSize = uiState.selectedBrushSize,
                    activeColor = if (uiState.isEraser) Color.White else uiState.selectedColor,
                    onBrushSizeSelected = viewModel::selectBrushSize,
                    onDismiss = viewModel::closeBrushSizePicker
                )
            }

            if (uiState.isStampPickerOpen) {
                StampPickerDialog(
                    selectedStamp = uiState.selectedStamp,
                    onStampSelected = viewModel::selectStamp,
                    onDismiss = viewModel::closeStampPicker
                )
            }

            if (uiState.isClearConfirmOpen) {
                ClearConfirmDialog(
                    onConfirm = viewModel::confirmClearCanvas,
                    onDismiss = viewModel::dismissClearConfirm
                )
            }
        }
    }
}
