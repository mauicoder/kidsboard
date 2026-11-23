package dev.mauicoder.kidsboard.ui

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import dev.mauicoder.kidsboard.audio.KidsSoundManager
import dev.mauicoder.kidsboard.model.BackgroundCategory
import dev.mauicoder.kidsboard.model.CanvasBackground
import dev.mauicoder.kidsboard.model.DrawingTool
import dev.mauicoder.kidsboard.model.StampRepository
import dev.mauicoder.kidsboard.ui.components.BrushSizeActionButton
import dev.mauicoder.kidsboard.ui.components.BrushSizePickerDialog
import dev.mauicoder.kidsboard.ui.components.CanvasBackgroundPickerDialog
import dev.mauicoder.kidsboard.ui.components.CanvasStyleActionButton
import dev.mauicoder.kidsboard.ui.components.ChunkyActionButton
import dev.mauicoder.kidsboard.ui.components.ClearConfirmDialog
import dev.mauicoder.kidsboard.ui.components.ColorPickerDialog
import dev.mauicoder.kidsboard.ui.components.DrawingBoardSheet
import dev.mauicoder.kidsboard.ui.components.MagicWandActionButton
import dev.mauicoder.kidsboard.ui.components.QuickColorGrid
import dev.mauicoder.kidsboard.ui.components.QuickColorRow
import dev.mauicoder.kidsboard.ui.components.QuickStickerGrid
import dev.mauicoder.kidsboard.ui.components.QuickStickerRow
import dev.mauicoder.kidsboard.ui.components.SaveActionButton
import dev.mauicoder.kidsboard.ui.components.SoundActionButton
import dev.mauicoder.kidsboard.ui.components.StampPickerDialog
import dev.mauicoder.kidsboard.ui.components.WarmDeskBackground
import dev.mauicoder.kidsboard.util.DrawingExporter
import dev.mauicoder.kidsboard.viewmodel.DrawingViewModel
import dev.mauicoder.kidsboard.viewmodel.SaveEvent

@Composable
fun KidsBoardScreen(
    viewModel: DrawingViewModel = viewModel(
        factory = DrawingViewModel.provideFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val soundManager = remember(context) { KidsSoundManager(context) }
    DisposableEffect(soundManager) {
        onDispose {
            soundManager.release()
        }
    }

    LaunchedEffect(uiState.isSoundEnabled) {
        soundManager.isSoundEnabled = uiState.isSoundEnabled
    }

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
            val maxVerticalPaper = (maxHeight - 270.dp).coerceAtLeast(180.dp)
            val paperSize = if (isLandscape) {
                (shortestEdge - (paperPadding * 2)).coerceAtLeast(180.dp)
            } else {
                minOf(shortestEdge - (paperPadding * 2), maxVerticalPaper)
            }

            if (isLandscape) {
                // LANDSCAPE: Left Dock (Colors/Brush/Sound) | Center (1:1 Paper) | Right Dock (Actions/Stickers)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Dock (Margins reclaimed for Colors, Brush & Sound)
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
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BrushSizeActionButton(
                                        brushSize = uiState.selectedBrushSize,
                                        selectedColor = uiState.selectedColor,
                                        isEraser = uiState.isEraser,
                                        isRainbow = uiState.isRainbow,
                                        onClick = {
                                            soundManager.playClick()
                                            viewModel.openBrushSizePicker()
                                        }
                                    )
                                    SoundActionButton(
                                        isSoundEnabled = uiState.isSoundEnabled,
                                        onClick = {
                                            viewModel.toggleSound()
                                            soundManager.playClick()
                                        }
                                    )
                                }

                                QuickColorGrid(
                                    selectedColor = uiState.selectedColor,
                                    isEraser = uiState.isEraser,
                                    isRainbow = uiState.isRainbow,
                                    onColorSelected = { color ->
                                        soundManager.playClick()
                                        viewModel.selectColor(color)
                                    },
                                    onOpenFullPicker = {
                                        soundManager.playClick()
                                        viewModel.openColorPicker()
                                    },
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
                            onStartStroke = { pointerId, point ->
                                soundManager.startDrawingLoop()
                                viewModel.startStroke(pointerId, point)
                            },
                            onAddPoint = viewModel::addPointToStroke,
                            onFinishStroke = { pointerId ->
                                viewModel.finishStroke(pointerId)
                                if (viewModel.uiState.value.activeStrokes.isEmpty()) {
                                    soundManager.stopDrawingLoop()
                                }
                            },
                            onFinishAllStrokes = {
                                viewModel.finishAllStrokes()
                                soundManager.stopDrawingLoop()
                            },
                            onAddStamp = { position ->
                                val isMagical = uiState.selectedStamp == StampRepository.FULL_RAINBOW ||
                                    uiState.selectedStamp == "🌈" ||
                                    uiState.selectedStamp == "✨" ||
                                    uiState.selectedStamp == "⭐"
                                soundManager.playStamp(isMagical = isMagical)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.addStamp(position)
                            },
                            onSizeChanged = viewModel::updateCanvasSize,
                            background = uiState.selectedBackground
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
                                            onClick = {
                                                soundManager.playClick()
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.undo()
                                            },
                                            enabled = uiState.canUndo
                                        )
                                        ChunkyActionButton(
                                            icon = Icons.AutoMirrored.Filled.Redo,
                                            contentDescription = "Redo",
                                            onClick = {
                                                soundManager.playClick()
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.redo()
                                            },
                                            enabled = uiState.canRedo
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        CanvasStyleActionButton(
                                            onClick = {
                                                soundManager.playClick()
                                                viewModel.openBackgroundPicker()
                                            }
                                        )
                                        if (uiState.selectedBackground.category == BackgroundCategory.COLORING) {
                                            MagicWandActionButton(
                                                isActive = uiState.isStayInsideLines,
                                                onClick = {
                                                    soundManager.playStamp(isMagical = true)
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    viewModel.toggleStayInsideLines()
                                                }
                                            )
                                        } else {
                                            SoundActionButton(
                                                isSoundEnabled = uiState.isSoundEnabled,
                                                onClick = {
                                                    viewModel.toggleSound()
                                                    soundManager.playClick()
                                                }
                                            )
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ChunkyActionButton(
                                            icon = Icons.Default.Delete,
                                            contentDescription = "Clear Canvas",
                                            onClick = {
                                                soundManager.playClick()
                                                viewModel.requestClearCanvas()
                                            },
                                            enabled = uiState.elements.isNotEmpty() || uiState.currentStroke != null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        SaveActionButton(
                                            isSaving = uiState.isSaving,
                                            onClick = {
                                                soundManager.playClick()
                                                viewModel.saveDrawing(context)
                                            },
                                            enabled = !uiState.isSaving && (uiState.elements.isNotEmpty() || uiState.currentStroke != null || uiState.selectedBackground != CanvasBackground.PlainWhite)
                                        )
                                    }
                                }

                                QuickStickerGrid(
                                    selectedStamp = uiState.selectedStamp,
                                    isStampActive = uiState.activeTool == DrawingTool.STAMP,
                                    selectedStampSize = uiState.selectedStampSize,
                                    onStampSelected = { stamp ->
                                        soundManager.playClick()
                                        viewModel.selectStamp(stamp)
                                    },
                                    onStampSizeSelected = { size ->
                                        soundManager.playClick()
                                        viewModel.selectStampSize(size)
                                    },
                                    onOpenFullPicker = {
                                        soundManager.playClick()
                                        viewModel.openStampPicker()
                                    },
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        }
                    }
                }
            } else {
                // PORTRAIT: Top Dock (Actions, Brush & Sound) | Center (1:1 Paper) | Bottom Dock (Colors & Stickers)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar: Canvas Setup (Left) & Sound / Share (Right)
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
                                CanvasStyleActionButton(
                                    onClick = {
                                        soundManager.playClick()
                                        viewModel.openBackgroundPicker()
                                    }
                                )
                                if (uiState.selectedBackground.category == BackgroundCategory.COLORING) {
                                    MagicWandActionButton(
                                        isActive = uiState.isStayInsideLines,
                                        onClick = {
                                            soundManager.playStamp(isMagical = true)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.toggleStayInsideLines()
                                        }
                                    )
                                }
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
                                SoundActionButton(
                                    isSoundEnabled = uiState.isSoundEnabled,
                                    onClick = {
                                        viewModel.toggleSound()
                                        soundManager.playClick()
                                    }
                                )
                                SaveActionButton(
                                    isSaving = uiState.isSaving,
                                    onClick = {
                                        soundManager.playClick()
                                        viewModel.saveDrawing(context)
                                    },
                                    enabled = !uiState.isSaving && (uiState.elements.isNotEmpty() || uiState.currentStroke != null || uiState.selectedBackground != CanvasBackground.PlainWhite)
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
                            onStartStroke = { pointerId, point ->
                                soundManager.startDrawingLoop()
                                viewModel.startStroke(pointerId, point)
                            },
                            onAddPoint = viewModel::addPointToStroke,
                            onFinishStroke = { pointerId ->
                                viewModel.finishStroke(pointerId)
                                if (viewModel.uiState.value.activeStrokes.isEmpty()) {
                                    soundManager.stopDrawingLoop()
                                }
                            },
                            onFinishAllStrokes = {
                                viewModel.finishAllStrokes()
                                soundManager.stopDrawingLoop()
                            },
                            onAddStamp = { position ->
                                val isMagical = uiState.selectedStamp == StampRepository.FULL_RAINBOW ||
                                    uiState.selectedStamp == "🌈" ||
                                    uiState.selectedStamp == "✨" ||
                                    uiState.selectedStamp == "⭐"
                                soundManager.playStamp(isMagical = isMagical)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.addStamp(position)
                            },
                            onSizeChanged = viewModel::updateCanvasSize,
                            background = uiState.selectedBackground
                        )
                    }

                    // Mid Action Bar (Drawing & Editing Tools: Brush Size, Undo, Redo, Clear)
                    Surface(
                        modifier = Modifier.padding(bottom = 6.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        tonalElevation = 4.dp,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BrushSizeActionButton(
                                brushSize = uiState.selectedBrushSize,
                                selectedColor = uiState.selectedColor,
                                isEraser = uiState.isEraser,
                                isRainbow = uiState.isRainbow,
                                onClick = {
                                    soundManager.playClick()
                                    viewModel.openBrushSizePicker()
                                }
                            )
                            ChunkyActionButton(
                                icon = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                onClick = {
                                    soundManager.playClick()
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.undo()
                                },
                                enabled = uiState.canUndo
                            )
                            ChunkyActionButton(
                                icon = Icons.AutoMirrored.Filled.Redo,
                                contentDescription = "Redo",
                                onClick = {
                                    soundManager.playClick()
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.redo()
                                },
                                enabled = uiState.canRedo
                            )
                            ChunkyActionButton(
                                icon = Icons.Default.Delete,
                                contentDescription = "Clear Canvas",
                                onClick = {
                                    soundManager.playClick()
                                    viewModel.requestClearCanvas()
                                },
                                enabled = uiState.elements.isNotEmpty() || uiState.currentStroke != null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
                                onColorSelected = { color ->
                                    soundManager.playClick()
                                    viewModel.selectColor(color)
                                },
                                onOpenFullPicker = {
                                    soundManager.playClick()
                                    viewModel.openColorPicker()
                                }
                            )
                            QuickStickerRow(
                                selectedStamp = uiState.selectedStamp,
                                isStampActive = uiState.activeTool == DrawingTool.STAMP,
                                selectedStampSize = uiState.selectedStampSize,
                                onStampSelected = { stamp ->
                                    soundManager.playClick()
                                    viewModel.selectStamp(stamp)
                                },
                                onStampSizeSelected = { size ->
                                    soundManager.playClick()
                                    viewModel.selectStampSize(size)
                                },
                                onOpenFullPicker = {
                                    soundManager.playClick()
                                    viewModel.openStampPicker()
                                }
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
                    onColorSelected = { color ->
                        soundManager.playClick()
                        viewModel.selectColor(color)
                    },
                    onDismiss = {
                        soundManager.playClick()
                        viewModel.closeColorPicker()
                    }
                )
            }

            if (uiState.isBrushSizePickerOpen) {
                BrushSizePickerDialog(
                    selectedBrushSize = uiState.selectedBrushSize,
                    activeColor = if (uiState.isEraser) Color.White else uiState.selectedColor,
                    isRainbow = uiState.isRainbow,
                    onBrushSizeSelected = { size ->
                        soundManager.playClick()
                        viewModel.selectBrushSize(size)
                    },
                    onDismiss = {
                        soundManager.playClick()
                        viewModel.closeBrushSizePicker()
                    }
                )
            }

            if (uiState.isStampPickerOpen) {
                StampPickerDialog(
                    selectedStamp = uiState.selectedStamp,
                    selectedStampSize = uiState.selectedStampSize,
                    onStampSelected = { stamp ->
                        soundManager.playClick()
                        viewModel.selectStamp(stamp)
                    },
                    onStampSizeSelected = { size ->
                        soundManager.playClick()
                        viewModel.selectStampSize(size)
                    },
                    onDismiss = {
                        soundManager.playClick()
                        viewModel.closeStampPicker()
                    }
                )
            }

            if (uiState.isBackgroundPickerOpen) {
                CanvasBackgroundPickerDialog(
                    selectedBackground = uiState.selectedBackground,
                    onBackgroundSelected = { bg ->
                        soundManager.playClick()
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.selectBackground(bg)
                    },
                    onDismiss = {
                        soundManager.playClick()
                        viewModel.closeBackgroundPicker()
                    }
                )
            }

            if (uiState.isClearConfirmOpen) {
                ClearConfirmDialog(
                    onConfirm = {
                        soundManager.playClearWhoosh()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.confirmClearCanvas()
                    },
                    onDismiss = {
                        soundManager.playClick()
                        viewModel.dismissClearConfirm()
                    }
                )
            }
        }
    }
}
