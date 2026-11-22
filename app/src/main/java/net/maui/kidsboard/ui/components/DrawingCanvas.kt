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
import androidx.compose.ui.draw.clipToBounds
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
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStamp
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.DrawingTool
import net.maui.kidsboard.model.StampRepository

@Composable
fun DrawingCanvas(
    elements: List<CanvasElement>,
    activeStrokes: Map<Long, DrawingStroke>,
    activeTool: DrawingTool,
    onStartStroke: (Long, Offset) -> Unit,
    onAddPoint: (Long, Offset) -> Unit,
    onFinishStroke: (Long) -> Unit,
    onFinishAllStrokes: () -> Unit,
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
            .clipToBounds()
            .background(backgroundColor)
            .onSizeChanged { size ->
                onSizeChanged(size)
            }
            .pointerInput(activeTool) {
                awaitEachGesture {
                    val firstDown = awaitFirstDown(requireUnconsumed = false)
                    firstDown.consume()

                    val clampedFirstPos = Offset(
                        x = firstDown.position.x.coerceIn(0f, size.width.toFloat()),
                        y = firstDown.position.y.coerceIn(0f, size.height.toFloat())
                    )

                    if (activeTool == DrawingTool.STAMP) {
                        onAddStamp(clampedFirstPos)
                    } else {
                        onStartStroke(firstDown.id.value, clampedFirstPos)
                    }

                    do {
                        val event = awaitPointerEvent()
                        for (change in event.changes) {
                            if (!change.previousPressed && change.pressed) {
                                // Additional finger touched down
                                change.consume()
                                val clampedPos = Offset(
                                    x = change.position.x.coerceIn(0f, size.width.toFloat()),
                                    y = change.position.y.coerceIn(0f, size.height.toFloat())
                                )
                                if (activeTool == DrawingTool.STAMP) {
                                    onAddStamp(clampedPos)
                                } else {
                                    onStartStroke(change.id.value, clampedPos)
                                }
                            } else if (change.previousPressed && change.pressed) {
                                // Finger dragged/moved
                                if (activeTool == DrawingTool.BRUSH) {
                                    if (change.position != change.previousPosition) {
                                        change.consume()
                                        val clampedPos = Offset(
                                            x = change.position.x.coerceIn(0f, size.width.toFloat()),
                                            y = change.position.y.coerceIn(0f, size.height.toFloat())
                                        )
                                        onAddPoint(change.id.value, clampedPos)
                                    }
                                }
                            } else if (change.previousPressed && !change.pressed) {
                                // Finger lifted
                                change.consume()
                                if (activeTool == DrawingTool.BRUSH) {
                                    onFinishStroke(change.id.value)
                                }
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    if (activeTool == DrawingTool.BRUSH) {
                        onFinishAllStrokes()
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

        // Draw all active in-progress multi-touch strokes
        activeStrokes.values.forEach { stroke ->
            drawStroke(stroke)
        }
    }
}

private fun DrawScope.drawStamp(stamp: DrawingStamp, paint: Paint) {
    if (stamp.emoji == StampRepository.FULL_RAINBOW) {
        with(FullRainbowStickerRenderer) {
            drawFullRainbow(stamp.position, stamp.size)
        }
    } else if (stamp.emoji == "🌈") {
        with(FullRainbowStickerRenderer) {
            drawRainbow180(stamp.position, stamp.size)
        }
    } else {
        paint.textSize = stamp.size
        val yPos = stamp.position.y - (paint.descent() + paint.ascent()) / 2f
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(stamp.emoji, stamp.position.x, yPos, paint)
        }
    }
}

private fun DrawScope.drawStroke(stroke: DrawingStroke) {
    val points = stroke.points
    if (points.isEmpty()) return

    if (stroke.isRainbow) {
        drawRainbowStroke(stroke)
        return
    }

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

private fun DrawScope.drawRainbowStroke(stroke: DrawingStroke) {
    val points = stroke.points
    if (points.isEmpty()) return

    if (points.size == 1) {
        drawCircle(
            color = ColorPalette.rainbowColorAt(0f),
            radius = stroke.strokeWidth / 2f,
            center = points.first()
        )
        return
    }

    val stepSize = maxOf(2f, minOf(6f, stroke.strokeWidth * 0.25f))
    var cumulativeDist = 0f
    var prevPoint = points[0]

    fun drawCurve(start: Offset, control: Offset, end: Offset) {
        val l1 = kotlin.math.hypot(end.x - start.x, end.y - start.y)
        val l2 = kotlin.math.hypot(control.x - start.x, control.y - start.y) +
                kotlin.math.hypot(end.x - control.x, end.y - control.y)
        val approxLength = (l1 + l2) / 2f
        val steps = maxOf(1, kotlin.math.ceil(approxLength / stepSize).toInt())

        for (s in 1..steps) {
            val t = s.toFloat() / steps
            val omt = 1f - t
            val x = omt * omt * start.x + 2f * omt * t * control.x + t * t * end.x
            val y = omt * omt * start.y + 2f * omt * t * control.y + t * t * end.y
            val currPoint = Offset(x, y)

            val segDist = kotlin.math.hypot(currPoint.x - prevPoint.x, currPoint.y - prevPoint.y)
            cumulativeDist += segDist
            val color = ColorPalette.rainbowColorAt(cumulativeDist)

            drawLine(
                color = color,
                start = prevPoint,
                end = currPoint,
                strokeWidth = stroke.strokeWidth,
                cap = StrokeCap.Round
            )
            prevPoint = currPoint
        }
    }

    // First segment: from points[0] curving toward midpoint of points[0] and points[1]
    val firstMid = Offset(
        (points[0].x + points[1].x) / 2f,
        (points[0].y + points[1].y) / 2f
    )
    drawCurve(points[0], points[0], firstMid)

    // Intermediate segments: curving smoothly between successive midpoints
    for (i in 1 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val nextMid = Offset((p0.x + p1.x) / 2f, (p0.y + p1.y) / 2f)
        val prevMid = if (i == 1) firstMid else Offset(
            (points[i - 1].x + p0.x) / 2f,
            (points[i - 1].y + p0.y) / 2f
        )
        drawCurve(prevMid, p0, nextMid)
    }

    // Final segment: straight line to points.last() matching regular path lineTo
    val lastMid = if (points.size == 2) {
        firstMid
    } else {
        Offset(
            (points[points.size - 2].x + points.last().x) / 2f,
            (points[points.size - 2].y + points.last().y) / 2f
        )
    }
    val finalControl = Offset(
        (lastMid.x + points.last().x) / 2f,
        (lastMid.y + points.last().y) / 2f
    )
    drawCurve(lastMid, finalControl, points.last())
}
