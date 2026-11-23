package dev.mauicoder.kidsboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.mauicoder.kidsboard.model.CanvasBackground
import dev.mauicoder.kidsboard.model.CanvasElement
import dev.mauicoder.kidsboard.model.DrawingStroke
import dev.mauicoder.kidsboard.model.DrawingTool

@Composable
fun DrawingBoardSheet(
    paperSize: Dp,
    elements: List<CanvasElement>,
    activeStrokes: Map<Long, DrawingStroke>,
    activeTool: DrawingTool,
    onStartStroke: (Long, Offset) -> Unit,
    onAddPoint: (Long, Offset) -> Unit,
    onFinishStroke: (Long) -> Unit,
    onFinishAllStrokes: () -> Unit,
    onAddStamp: (Offset) -> Unit,
    onSizeChanged: (IntSize) -> Unit,
    modifier: Modifier = Modifier,
    background: CanvasBackground = CanvasBackground.PlainWhite
) {
    val surfaceColor = if (background.isDark) Color(0xFF1E2621) else Color.White
    val borderColor = if (background.isDark) Color(0xFF333E37) else WarmDeskBorder

    Surface(
        modifier = modifier
            .size(paperSize)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(20.dp)),
        color = surfaceColor
    ) {
        DrawingCanvas(
            elements = elements,
            activeStrokes = activeStrokes,
            activeTool = activeTool,
            onStartStroke = onStartStroke,
            onAddPoint = onAddPoint,
            onFinishStroke = onFinishStroke,
            onFinishAllStrokes = onFinishAllStrokes,
            onAddStamp = onAddStamp,
            onSizeChanged = onSizeChanged,
            background = background
        )
    }
}
