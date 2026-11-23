package dev.mauicoder.kidsboard.ui.components.background

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.RectF as AndroidRectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import dev.mauicoder.kidsboard.model.BackgroundCategory
import dev.mauicoder.kidsboard.model.CanvasBackground
import dev.mauicoder.kidsboard.model.ColoringOutlineTemplates
import dev.mauicoder.kidsboard.model.OutlineElement
import dev.mauicoder.kidsboard.model.PathAction

object CanvasBackgroundRenderer {

    // Colors
    private val ChalkboardColor = Color(0xFF1E2621)
    private val ChalkboardFrameColor = Color(0xFF333E37)
    private val GridLineColor = Color(0xFFD6E4EE)
    private val NotebookLineColor = Color(0xFFDCE6EF)
    private val NotebookMarginColor = Color(0xFFF3AEAE)
    private val OutlineColor = Color(0xFF2B3336)
    private val ChalkOutlineColor = Color(0xFFEBEBEB)

    // --- COMPOSE DRAWSCOPE RENDERING ---

    fun DrawScope.drawCanvasBackground(background: CanvasBackground) {
        drawPaperBase(background)
        drawColoringOutlines(background)
    }

    fun DrawScope.drawPaperBase(background: CanvasBackground) {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) return

        when (background) {
            CanvasBackground.PlainWhite -> {
                drawRect(color = Color.White, size = size)
            }
            CanvasBackground.Chalkboard -> {
                drawRect(color = ChalkboardColor, size = size)
                // Subtle inner chalkboard rim
                val frameThickness = (width * 0.015f).coerceIn(4f, 12f)
                drawRect(
                    color = ChalkboardFrameColor,
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = frameThickness)
                )
            }
            CanvasBackground.GridPaper -> {
                drawRect(color = Color(0xFFFCFDFD), size = size)
                val spacing = (width / 20f).coerceIn(24f, 48f)
                var x = spacing
                while (x < width) {
                    drawLine(
                        color = GridLineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1.2f
                    )
                    x += spacing
                }
                var y = spacing
                while (y < height) {
                    drawLine(
                        color = GridLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.2f
                    )
                    y += spacing
                }
            }
            CanvasBackground.LinedPaper -> {
                drawRect(color = Color(0xFFFCFDFD), size = size)
                val spacing = (height / 18f).coerceIn(28f, 52f)
                var y = spacing * 1.5f
                while (y < height) {
                    drawLine(
                        color = NotebookLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.4f
                    )
                    y += spacing
                }
                // Vertical pink margin
                val marginX = width * 0.12f
                drawLine(
                    color = NotebookMarginColor,
                    start = Offset(marginX, 0f),
                    end = Offset(marginX, height),
                    strokeWidth = 2.0f
                )
            }
            else -> {
                // Default background for coloring templates is clean white
                drawRect(color = Color.White, size = size)
            }
        }
    }

    fun DrawScope.drawColoringOutlines(background: CanvasBackground) {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) return

        if (background.category == BackgroundCategory.COLORING) {
            val elements = ColoringOutlineTemplates.getTemplate(background)
            val strokeWidth = (width / 100f).coerceIn(3.5f, 7.5f)
            val color = if (background.isDark) ChalkOutlineColor else OutlineColor

            for (element in elements) {
                when (element) {
                    is OutlineElement.Circle -> {
                        val center = Offset(element.cx * width, element.cy * height)
                        val radius = element.radius * width
                        if (element.isFilled) {
                            drawCircle(color = color, radius = radius, center = center, style = Fill)
                        } else {
                            drawCircle(
                                color = color,
                                radius = radius,
                                center = center,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                    is OutlineElement.Oval -> {
                        val topLeft = Offset((element.cx - element.rx) * width, (element.cy - element.ry) * height)
                        val ovalSize = Size(element.rx * 2f * width, element.ry * 2f * height)
                        if (element.isFilled) {
                            drawOval(color = color, topLeft = topLeft, size = ovalSize, style = Fill)
                        } else {
                            drawOval(
                                color = color,
                                topLeft = topLeft,
                                size = ovalSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                    is OutlineElement.Path -> {
                        val path = buildComposePath(element.actions, width, height, element.isClosed)
                        if (element.isFilled) {
                            drawPath(path = path, color = color, style = Fill)
                        } else {
                            drawPath(
                                path = path,
                                color = color,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun buildComposePath(
        actions: List<PathAction>,
        width: Float,
        height: Float,
        isClosed: Boolean
    ): Path {
        val path = Path()
        for (action in actions) {
            when (action) {
                is PathAction.MoveTo -> path.moveTo(action.x * width, action.y * height)
                is PathAction.LineTo -> path.lineTo(action.x * width, action.y * height)
                is PathAction.QuadTo -> path.quadraticTo(
                    action.cx * width,
                    action.cy * height,
                    action.x * width,
                    action.y * height
                )
                is PathAction.CubicTo -> path.cubicTo(
                    action.c1x * width,
                    action.c1y * height,
                    action.c2x * width,
                    action.c2y * height,
                    action.x * width,
                    action.y * height
                )
            }
        }
        if (isClosed) {
            path.close()
        }
        return path
    }

    // --- ANDROID CANVAS RENDERING (FOR BITMAP EXPORT) ---

    fun drawToAndroidCanvas(
        canvas: Canvas,
        background: CanvasBackground,
        width: Float,
        height: Float
    ) {
        drawPaperBaseToAndroidCanvas(canvas, background, width, height)
        drawColoringOutlinesToAndroidCanvas(canvas, background, width, height)
    }

    fun drawPaperBaseToAndroidCanvas(
        canvas: Canvas,
        background: CanvasBackground,
        width: Float,
        height: Float
    ) {
        if (width <= 0f || height <= 0f) return

        val bgPaint = AndroidPaint().apply {
            isAntiAlias = true
            style = AndroidPaint.Style.FILL
        }

        val linePaint = AndroidPaint().apply {
            isAntiAlias = true
            style = AndroidPaint.Style.STROKE
            strokeCap = AndroidPaint.Cap.ROUND
            strokeJoin = AndroidPaint.Join.ROUND
        }

        when (background) {
            CanvasBackground.PlainWhite -> {
                bgPaint.color = AndroidColor.WHITE
                canvas.drawRect(0f, 0f, width, height, bgPaint)
            }
            CanvasBackground.Chalkboard -> {
                bgPaint.color = ChalkboardColor.toArgb()
                canvas.drawRect(0f, 0f, width, height, bgPaint)

                val frameThickness = (width * 0.015f).coerceIn(4f, 12f)
                linePaint.color = ChalkboardFrameColor.toArgb()
                linePaint.strokeWidth = frameThickness
                canvas.drawRect(0f, 0f, width, height, linePaint)
            }
            CanvasBackground.GridPaper -> {
                bgPaint.color = AndroidColor.rgb(251, 251, 251)
                canvas.drawRect(0f, 0f, width, height, bgPaint)

                linePaint.color = GridLineColor.toArgb()
                linePaint.strokeWidth = 1.2f
                val spacing = (width / 20f).coerceIn(24f, 48f)
                var x = spacing
                while (x < width) {
                    canvas.drawLine(x, 0f, x, height, linePaint)
                    x += spacing
                }
                var y = spacing
                while (y < height) {
                    canvas.drawLine(0f, y, width, y, linePaint)
                    y += spacing
                }
            }
            CanvasBackground.LinedPaper -> {
                bgPaint.color = AndroidColor.rgb(251, 251, 251)
                canvas.drawRect(0f, 0f, width, height, bgPaint)

                linePaint.color = NotebookLineColor.toArgb()
                linePaint.strokeWidth = 1.4f
                val spacing = (height / 18f).coerceIn(28f, 52f)
                var y = spacing * 1.5f
                while (y < height) {
                    canvas.drawLine(0f, y, width, y, linePaint)
                    y += spacing
                }

                // Vertical margin line
                linePaint.color = NotebookMarginColor.toArgb()
                linePaint.strokeWidth = 2.0f
                val marginX = width * 0.12f
                canvas.drawLine(marginX, 0f, marginX, height, linePaint)
            }
            else -> {
                bgPaint.color = AndroidColor.WHITE
                canvas.drawRect(0f, 0f, width, height, bgPaint)
            }
        }
    }

    fun drawColoringOutlinesToAndroidCanvas(
        canvas: Canvas,
        background: CanvasBackground,
        width: Float,
        height: Float
    ) {
        if (width <= 0f || height <= 0f) return
        if (background.category != BackgroundCategory.COLORING) return

        val strokeWidth = (width / 100f).coerceIn(3.5f, 7.5f)
        val outlineColor = (if (background.isDark) ChalkOutlineColor else OutlineColor).toArgb()

        val strokePaint = AndroidPaint().apply {
            isAntiAlias = true
            style = AndroidPaint.Style.STROKE
            color = outlineColor
            this.strokeWidth = strokeWidth
            strokeCap = AndroidPaint.Cap.ROUND
            strokeJoin = AndroidPaint.Join.ROUND
        }

        val fillPaint = AndroidPaint().apply {
            isAntiAlias = true
            style = AndroidPaint.Style.FILL
            color = outlineColor
        }

        val ovalRect = AndroidRectF()
        val elements = ColoringOutlineTemplates.getTemplate(background)

        for (element in elements) {
            when (element) {
                is OutlineElement.Circle -> {
                    val cx = element.cx * width
                    val cy = element.cy * height
                    val r = element.radius * width
                    if (element.isFilled) {
                        canvas.drawCircle(cx, cy, r, fillPaint)
                    } else {
                        canvas.drawCircle(cx, cy, r, strokePaint)
                    }
                }
                is OutlineElement.Oval -> {
                    val left = (element.cx - element.rx) * width
                    val top = (element.cy - element.ry) * height
                    val right = (element.cx + element.rx) * width
                    val bottom = (element.cy + element.ry) * height
                    ovalRect.set(left, top, right, bottom)
                    if (element.isFilled) {
                        canvas.drawOval(ovalRect, fillPaint)
                    } else {
                        canvas.drawOval(ovalRect, strokePaint)
                    }
                }
                is OutlineElement.Path -> {
                    val path = buildAndroidPath(element.actions, width, height, element.isClosed)
                    if (element.isFilled) {
                        canvas.drawPath(path, fillPaint)
                    } else {
                        canvas.drawPath(path, strokePaint)
                    }
                }
            }
        }
    }

    private fun buildAndroidPath(
        actions: List<PathAction>,
        width: Float,
        height: Float,
        isClosed: Boolean
    ): AndroidPath {
        val path = AndroidPath()
        for (action in actions) {
            when (action) {
                is PathAction.MoveTo -> path.moveTo(action.x * width, action.y * height)
                is PathAction.LineTo -> path.lineTo(action.x * width, action.y * height)
                is PathAction.QuadTo -> path.quadTo(
                    action.cx * width,
                    action.cy * height,
                    action.x * width,
                    action.y * height
                )
                is PathAction.CubicTo -> path.cubicTo(
                    action.c1x * width,
                    action.c1y * height,
                    action.c2x * width,
                    action.c2y * height,
                    action.x * width,
                    action.y * height
                )
            }
        }
        if (isClosed) {
            path.close()
        }
        return path
    }
}
