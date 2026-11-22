package net.maui.kidsboard.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import net.maui.kidsboard.model.CanvasElement
import net.maui.kidsboard.model.DrawingStamp
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.DrawingTool

@Composable
fun DrawingCanvas(
    elements: List<CanvasElement>,
    currentStroke: DrawingStroke?,
    activeTool: DrawingTool,
    onStartStroke: (Offset) -> Unit,
    onAddPoint: (Offset) -> Unit,
    onFinishStroke: () -> Unit,
    onAddStamp: (Offset) -> Unit,
    modifier: Modifier = Modifier,
    onSizeChanged: (IntSize) -> Unit = {},
    backgroundColor: Color = Color.White
) {
    val stampPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .onSizeChanged { size ->
                onSizeChanged(size)
            }
            .pointerInput(activeTool) {
                if (activeTool == DrawingTool.STAMP) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        down.consume()
                        onAddStamp(down.position)
                        do {
                            val event = awaitPointerEvent()
                        } while (event.changes.any { it.pressed })
                    }
                } else {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        down.consume()
                        onStartStroke(down.position)
                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            if (change.pressed) {
                                change.consume()
                                onAddPoint(change.position)
                            }
                        } while (event.changes.any { it.pressed })
                        onFinishStroke()
                    }
                }
            }
    ) {
        // Draw committed elements in chronological order
        elements.forEach { element ->
            when (element) {
                is DrawingStroke -> drawStroke(element)
                is DrawingStamp -> drawStamp(element, stampPaint)
            }
        }

        // Draw current in-progress stroke
        currentStroke?.let { stroke ->
            drawStroke(stroke)
        }
    }
}

private fun DrawScope.drawStamp(stamp: DrawingStamp, paint: Paint) {
    paint.textSize = stamp.size
    val yPos = stamp.position.y - (paint.descent() + paint.ascent()) / 2f
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawText(stamp.emoji, stamp.position.x, yPos, paint)
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
