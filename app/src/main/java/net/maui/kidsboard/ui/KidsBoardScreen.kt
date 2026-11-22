package net.maui.kidsboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import net.maui.kidsboard.model.DrawingTool
import net.maui.kidsboard.ui.components.BrushSizeActionButton
import net.maui.kidsboard.ui.components.BrushSizePickerDialog
import net.maui.kidsboard.ui.components.ChunkyActionButton
import net.maui.kidsboard.ui.components.ClearConfirmDialog
import net.maui.kidsboard.ui.components.ColorPickerDialog
import net.maui.kidsboard.ui.components.DrawingBoardSheet
import net.maui.kidsboard.ui.components.QuickColorGrid
import net.maui.kidsboard.ui.components.QuickColorRow
import net.maui.kidsboard.ui.components.QuickStickerGrid
import net.maui.kidsboard.ui.components.QuickStickerRow
import net.maui.kidsboard.ui.components.SaveActionButton
import net.maui.kidsboard.ui.components.StampPickerDialog
import net.maui.kidsboard.ui.components.WarmDeskBackground
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
                        message = "Drawing saved to Pictures! 🎨",
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(WarmDeskBackground)
        ) {
            val isLandscape = maxWidth > maxHeight
            val shortestEdge = minOf(maxWidth, maxHeight)
            val paperPadding = 12.dp
            val paperSize = (shortestEdge - (paperPadding * 2)).coerceAtLeast(180.dp)

            if (isLandscape) {
                // LANDSCAPE: Left Dock (Colors/Brush) | Center (1:1 Paper) | Right Dock (Actions/Stickers)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Dock (Margins reclaimed for Colors & Brush)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Left + WindowInsetsSides.Vertical).asPaddingValues())
                            .padding(start = 12.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            tonalElevation = 4.dp,
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                BrushSizeActionButton(
                                    brushSize = uiState.selectedBrushSize,
                                    selectedColor = uiState.selectedColor,
                                    isEraser = uiState.isEraser,
                                    isRainbow = uiState.isRainbow,
                                    onClick = viewModel::openBrushSizePicker
                                )

                                QuickColorGrid(
                                    selectedColor = uiState.selectedColor,
                                    isEraser = uiState.isEraser,
                                    isRainbow = uiState.isRainbow,
                                    onColorSelected = viewModel::selectColor,
                                    onOpenFullPicker = viewModel::openColorPicker,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        }
                    }

                    // Center: 1:1 Square Drawing Board Paper
                    Box(
                        modifier = Modifier.size(shortestEdge),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawingBoardSheet(
                            paperSize = paperSize,
                            elements = uiState.elements,
                            activeStrokes = uiState.activeStrokes,
                            activeTool = uiState.activeTool,
                            onStartStroke = viewModel::startStroke,
                            onAddPoint = viewModel::addPointToStroke,
                            onFinishStroke = viewModel::finishStroke,
                            onFinishAllStrokes = viewModel::finishAllStrokes,
                            onAddStamp = viewModel::addStamp,
                            onSizeChanged = viewModel::updateCanvasSize
                        )
                    }

                    // Right Dock (Margins reclaimed for Actions & Stickers)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Right + WindowInsetsSides.Vertical).asPaddingValues())
                            .padding(start = 6.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            tonalElevation = 4.dp,
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Actions
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ChunkyActionButton(
                                            icon = Icons.AutoMirrored.Filled.Undo,
                                            contentDescription = "Undo",
                                            onClick = viewModel::undo,
                                            enabled = uiState.canUndo
                                        )
                                        ChunkyActionButton(
                                            icon = Icons.AutoMirrored.Filled.Redo,
                                            contentDescription = "Redo",
                                            onClick = viewModel::redo,
                                            enabled = uiState.canRedo
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ChunkyActionButton(
                                            icon = Icons.Default.Delete,
                                            contentDescription = "Clear Canvas",
                                            onClick = viewModel::requestClearCanvas,
                                            enabled = uiState.elements.isNotEmpty() || uiState.currentStroke != null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        SaveActionButton(
                                            isSaving = uiState.isSaving,
                                            onClick = { viewModel.saveDrawing(context) },
                                            enabled = !uiState.isSaving && (uiState.elements.isNotEmpty() || uiState.currentStroke != null)
                                        )
                                    }
                                }

                                QuickStickerGrid(
                                    selectedStamp = uiState.selectedStamp,
                                    isStampActive = uiState.activeTool == DrawingTool.STAMP,
                                    onStampSelected = viewModel::selectStamp,
                                    onOpenFullPicker = viewModel::openStampPicker,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        }
                    }
                }
            } else {
                // PORTRAIT: Top Dock (Actions) | Center (1:1 Paper) | Bottom Dock (Colors & Stickers)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Dock (Actions & Brush Size)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal).asPaddingValues())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(32.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            tonalElevation = 4.dp,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrushSizeActionButton(
                                    brushSize = uiState.selectedBrushSize,
                                    selectedColor = uiState.selectedColor,
                                    isEraser = uiState.isEraser,
                                    isRainbow = uiState.isRainbow,
                                    onClick = viewModel::openBrushSizePicker
                                )
                                ChunkyActionButton(
                                    icon = Icons.Default.Delete,
                                    contentDescription = "Clear Canvas",
                                    onClick = viewModel::requestClearCanvas,
                                    enabled = uiState.elements.isNotEmpty() || uiState.currentStroke != null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(32.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            tonalElevation = 4.dp,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChunkyActionButton(
                                    icon = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = "Undo",
                                    onClick = viewModel::undo,
                                    enabled = uiState.canUndo
                                )
                                ChunkyActionButton(
                                    icon = Icons.AutoMirrored.Filled.Redo,
                                    contentDescription = "Redo",
                                    onClick = viewModel::redo,
                                    enabled = uiState.canRedo
                                )
                                SaveActionButton(
                                    isSaving = uiState.isSaving,
                                    onClick = { viewModel.saveDrawing(context) },
                                    enabled = !uiState.isSaving && (uiState.elements.isNotEmpty() || uiState.currentStroke != null)
                                )
                            }
                        }
                    }

                    // Center: 1:1 Square Drawing Board Paper
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawingBoardSheet(
                            paperSize = paperSize,
                            elements = uiState.elements,
                            activeStrokes = uiState.activeStrokes,
                            activeTool = uiState.activeTool,
                            onStartStroke = viewModel::startStroke,
                            onAddPoint = viewModel::addPointToStroke,
                            onFinishStroke = viewModel::finishStroke,
                            onFinishAllStrokes = viewModel::finishAllStrokes,
                            onAddStamp = viewModel::addStamp,
                            onSizeChanged = viewModel::updateCanvasSize
                        )
                    }

                    // Bottom Dock (Margins reclaimed for Colors & Stickers Trays)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal).asPaddingValues())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        tonalElevation = 4.dp,
                        shadowElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickColorRow(
                                selectedColor = uiState.selectedColor,
                                isEraser = uiState.isEraser,
                                isRainbow = uiState.isRainbow,
                                onColorSelected = viewModel::selectColor,
                                onOpenFullPicker = viewModel::openColorPicker
                            )
                            QuickStickerRow(
                                selectedStamp = uiState.selectedStamp,
                                isStampActive = uiState.activeTool == DrawingTool.STAMP,
                                onStampSelected = viewModel::selectStamp,
                                onOpenFullPicker = viewModel::openStampPicker
                            )
                        }
                    }
                }
            }

            // Dialogs
            if (uiState.isColorPickerOpen) {
                ColorPickerDialog(
                    selectedColor = uiState.selectedColor,
                    isRainbow = uiState.isRainbow,
                    onColorSelected = viewModel::selectColor,
                    onDismiss = viewModel::closeColorPicker
                )
            }

            if (uiState.isBrushSizePickerOpen) {
                BrushSizePickerDialog(
                    selectedBrushSize = uiState.selectedBrushSize,
                    activeColor = if (uiState.isEraser) Color.White else uiState.selectedColor,
                    isRainbow = uiState.isRainbow,
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
