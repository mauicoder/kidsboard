package net.maui.kidsboard.ui.components

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
import net.maui.kidsboard.model.CanvasElement
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.DrawingTool

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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(paperSize)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(BorderStroke(1.5.dp, WarmDeskBorder), RoundedCornerShape(20.dp)),
        color = Color.White
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
            onSizeChanged = onSizeChanged
        )
    }
}
