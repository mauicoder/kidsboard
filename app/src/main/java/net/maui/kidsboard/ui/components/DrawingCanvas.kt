package net.maui.kidsboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import net.maui.kidsboard.model.DrawingStroke

@Composable
fun DrawingCanvas(
    strokes: List<DrawingStroke>,
    currentStroke: DrawingStroke?,
    onStartStroke: (Offset) -> Unit,
    onAddPoint: (Offset) -> Unit,
    onFinishStroke: () -> Unit,
    modifier: Modifier = Modifier,
    onSizeChanged: (IntSize) -> Unit = {},
    backgroundColor: Color = Color.White
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .onSizeChanged { size ->
                onSizeChanged(size)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onStartStroke(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onAddPoint(change.position)
                    },
                    onDragEnd = {
                        onFinishStroke()
                    },
                    onDragCancel = {
                        onFinishStroke()
                    }
                )
            }
    ) {
        // Draw committed strokes
        strokes.forEach { stroke ->
            drawStroke(stroke)
        }

        // Draw current in-progress stroke
        currentStroke?.let { stroke ->
            drawStroke(stroke)
        }
    }
}

private fun DrawScope.drawStroke(stroke: DrawingStroke) {
    val points = stroke.points
    if (points.isEmpty()) return

    if (points.size == 1) {
        drawCircle(
            color = stroke.color,
            radius = stroke.strokeWidth / 2,
            center = points.first()
        )
        return
    }

    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p1 = points[i]
            val midX = (p0.x + p1.x) / 2f
            val midY = (p0.y + p1.y) / 2f
            quadraticTo(p0.x, p0.y, midX, midY)
        }
        lineTo(points.last().x, points.last().y)
    }

    drawPath(
        path = path,
        color = stroke.color,
        style = Stroke(
            width = stroke.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
