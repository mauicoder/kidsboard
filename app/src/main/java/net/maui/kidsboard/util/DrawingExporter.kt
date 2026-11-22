package net.maui.kidsboard.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import net.maui.kidsboard.model.CanvasElement
import net.maui.kidsboard.model.ColorPalette
import net.maui.kidsboard.model.DrawingStamp
import net.maui.kidsboard.model.DrawingStroke
import net.maui.kidsboard.ui.components.sticker.StickerRegistry

open class DrawingExporter {

    open fun renderToBitmap(
        elements: List<CanvasElement>,
        width: Int,
        height: Int
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fill background with white
        canvas.drawColor(AndroidColor.WHITE)

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        val stampPaint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        for (element in elements) {
            when (element) {
                is DrawingStroke -> {
                    val points = element.points
                    if (points.isEmpty()) continue

                    strokePaint.strokeWidth = element.strokeWidth

                    if (element.isRainbow) {
                        if (points.size == 1) {
                            strokePaint.color = ColorPalette.rainbowColorIntAt(0f)
                            canvas.drawCircle(points[0].x, points[0].y, element.strokeWidth / 2f, strokePaint)
                            continue
                        }

                        RainbowStrokeGeometry.forEachSubdivision(points, element.strokeWidth) { p0, p1, dist ->
                            strokePaint.color = ColorPalette.rainbowColorIntAt(dist)
                            canvas.drawLine(p0.x, p0.y, p1.x, p1.y, strokePaint)
                        }
                        continue
                    }

                    strokePaint.color = element.color.toArgb()

                    if (points.size == 1) {
                        canvas.drawCircle(points[0].x, points[0].y, element.strokeWidth / 2f, strokePaint)
                        continue
                    }

                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val p0 = points[i - 1]
                            val p1 = points[i]
                            val midX = (p0.x + p1.x) / 2f
                            val midY = (p0.y + p1.y) / 2f
                            quadTo(p0.x, p0.y, midX, midY)
                        }
                        lineTo(points.last().x, points.last().y)
                    }

                    canvas.drawPath(path, strokePaint)
                }
                is DrawingStamp -> {
                    val customSticker = StickerRegistry.find(element.emoji)
                    if (customSticker != null) {
                        customSticker.drawToCanvas(
                            canvas,
                            element.position.x,
                            element.position.y,
                            element.size
                        )
                    } else {
                        stampPaint.textSize = element.size
                        val yPos = element.position.y - (stampPaint.descent() + stampPaint.ascent()) / 2f
                        canvas.drawText(element.emoji, element.position.x, yPos, stampPaint)
                    }
                }
            }
        }

        return bitmap
    }

    open fun exportToGallery(
        context: Context,
        elements: List<CanvasElement>,
        width: Int,
        height: Int
    ): Result<Uri> {
        return runCatching {
            val bitmap = renderToBitmap(elements, width, height)
            val filename = "KidsBoard_${System.currentTimeMillis()}.png"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/KidsBoard"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IllegalStateException("Could not create MediaStore entry")

            resolver.openOutputStream(uri)?.use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IllegalStateException("Failed to encode bitmap to PNG")
                }
            } ?: throw IllegalStateException("Could not open output stream for MediaStore URI")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            uri
        }
    }

    companion object {
        fun shareDrawing(context: Context, uri: Uri) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share your masterpiece")
            context.startActivity(chooser)
        }
    }
}
