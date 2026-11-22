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
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import net.maui.kidsboard.model.CanvasBackground
import net.maui.kidsboard.model.CanvasElement
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.ColoringRegion
import net.maui.kidsboard.model.ColoringRegionManager
import net.maui.kidsboard.model.DrawingStamp
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.model.DrawingTool
import net.maui.kidsboard.ui.components.background.CanvasBackgroundRenderer
import net.maui.kidsboard.ui.components.sticker.StickerRegistry
import net.maui.kidsboard.util.RainbowStrokeGeometry

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
    background: CanvasBackground = CanvasBackground.PlainWhite
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
        // 1. Draw background paper style base
        CanvasBackgroundRenderer.run {
            drawPaperBase(background)
        }

        // 2. Draw user strokes with clipping (so strokes stay inside their tapped region)
        elements.forEach { element ->
            if (element is DrawingStroke) {
                drawClippedStroke(element, background)
            }
        }

        // 3. Draw template black outlines on top of user strokes so lines stay crisp and sharp
        CanvasBackgroundRenderer.run {
            drawColoringOutlines(background)
        }

        // 4. Draw placed stamps/stickers on top
        elements.forEach { element ->
            if (element is DrawingStamp) {
                drawStamp(element, stampPaint)
            }
        }

        // 5. Draw active in-progress multi-touch strokes (clipped in real-time)
        activeStrokes.values.forEach { stroke ->
            drawClippedStroke(stroke, background)
        }
    }
}

private fun DrawScope.drawClippedStroke(stroke: DrawingStroke, background: CanvasBackground) {
    if (stroke.clipRegionId == null) {
        drawStroke(stroke)
        return
    }

    if (stroke.clipRegionId == ColoringRegion.OUTSIDE_REGION_ID) {
        val unionPath = ColoringRegionManager.getCharacterUnionComposePath(background, size.width, size.height)
        clipPath(unionPath, clipOp = ClipOp.Difference) {
            drawStroke(stroke)
        }
    } else {
        val region = ColoringRegionManager.getRegionById(background, stroke.clipRegionId)
        if (region != null) {
            val regionPath = region.toComposePath(size.width, size.height)
            clipPath(regionPath, clipOp = ClipOp.Intersect) {
                drawStroke(stroke)
            }
        } else {
            drawStroke(stroke)
        }
    }
}

private fun DrawScope.drawStamp(stamp: DrawingStamp, paint: Paint) {
    val customSticker = StickerRegistry.find(stamp.emoji)
    if (customSticker != null) {
        with(customSticker) {
            draw(stamp.position, stamp.size)
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

    RainbowStrokeGeometry.forEachSubdivision(points, stroke.strokeWidth) { p0, p1, dist ->
        drawLine(
            color = ColorPalette.rainbowColorAt(dist),
            start = p0,
            end = p1,
            strokeWidth = stroke.strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
