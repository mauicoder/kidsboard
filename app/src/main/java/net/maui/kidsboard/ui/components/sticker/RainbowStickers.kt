package net.maui.kidsboard.ui.components.sticker

import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import net.maui.kidsboard.model.StampRepository

/**
 * Shared palette for rainbow stickers.
 */
object RainbowStickerColors {
    val ComposeColors = listOf(
        Color(0xFFFF3B30), // Red
        Color(0xFFFF9500), // Orange
        Color(0xFFFFCC00), // Yellow
        Color(0xFF34C759), // Green
        Color(0xFF00C7BE), // Cyan
        Color(0xFF007AFF), // Blue
        Color(0xFF5856D6)  // Violet
    )

    val AndroidColors = intArrayOf(
        0xFFFF3B30.toInt(),
        0xFFFF9500.toInt(),
        0xFFFFCC00.toInt(),
        0xFF34C759.toInt(),
        0xFF00C7BE.toInt(),
        0xFF007AFF.toInt(),
        0xFF5856D6.toInt()
    )
}

/**
 * 360° Circular Rainbow with smiling sun, clouds, and sparkles.
 */
object FullRainbowSticker : CustomSticker {
    override val id: String = StampRepository.FULL_RAINBOW
    override val contentDescription: String = "Full Rainbow stamp"

    override fun DrawScope.draw(center: Offset, size: Float) {
        val radius = size / 2f
        val bandWidth = (radius * 0.44f) / 7f

        // Outer glow
        drawCircle(color = Color(0x10000000), radius = radius * 0.98f, center = center)

        // Soft sky-blue center
        val innerRadius = radius * 0.50f
        drawCircle(color = Color(0xFFF0F8FF), radius = innerRadius, center = center)

        // 360° Bands
        val outerR = radius * 0.94f
        for (i in RainbowStickerColors.ComposeColors.indices) {
            val r = outerR - (i * bandWidth) - (bandWidth / 2f)
            drawCircle(
                color = RainbowStickerColors.ComposeColors[i],
                radius = r,
                center = center,
                style = Stroke(width = bandWidth + 0.6f)
            )
        }

        // Cheerful Sun
        val sunRadius = innerRadius * 0.42f
        drawCircle(color = Color(0x33FFD700), radius = sunRadius * 1.35f, center = center)
        drawCircle(color = Color(0xFFFFD700), radius = sunRadius, center = center)
        drawCircle(color = Color(0xFFFFE600), radius = sunRadius * 0.72f, center = center)

        val eyeRadius = radius * 0.024f
        val eyeOffsetY = radius * 0.035f
        val eyeOffsetX = radius * 0.065f
        val eyeColor = Color(0xFF5D4037)
        drawCircle(eyeColor, eyeRadius, Offset(center.x - eyeOffsetX, center.y - eyeOffsetY))
        drawCircle(eyeColor, eyeRadius, Offset(center.x + eyeOffsetX, center.y - eyeOffsetY))

        val mouthWidth = radius * 0.09f
        val mouthHeight = radius * 0.05f
        drawArc(
            color = eyeColor,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(center.x - mouthWidth / 2f, center.y - radius * 0.01f),
            size = Size(mouthWidth, mouthHeight),
            style = Stroke(width = maxOf(1.5f, radius * 0.02f), cap = StrokeCap.Round)
        )

        val cheekRadius = radius * 0.025f
        val cheekColor = Color(0x66FF4081)
        drawCircle(cheekColor, cheekRadius, Offset(center.x - eyeOffsetX - eyeRadius * 1.2f, center.y))
        drawCircle(cheekColor, cheekRadius, Offset(center.x + eyeOffsetX + eyeRadius * 1.2f, center.y))

        // Clouds
        fun drawCloud(cloudCenter: Offset) {
            val r = radius * 0.16f
            val shadowColor = Color(0x18000000)
            drawCircle(shadowColor, r * 1.05f, Offset(cloudCenter.x - r * 0.85f, cloudCenter.y + r * 0.15f))
            drawCircle(shadowColor, r * 1.35f, Offset(cloudCenter.x, cloudCenter.y - r * 0.35f))
            drawCircle(shadowColor, r * 1.05f, Offset(cloudCenter.x + r * 0.85f, cloudCenter.y + r * 0.15f))

            drawCircle(Color.White, r * 0.95f, Offset(cloudCenter.x - r * 0.85f, cloudCenter.y + r * 0.05f))
            drawCircle(Color.White, r * 1.30f, Offset(cloudCenter.x, cloudCenter.y - r * 0.38f))
            drawCircle(Color.White, r * 0.95f, Offset(cloudCenter.x + r * 0.85f, cloudCenter.y + r * 0.05f))
            drawCircle(Color.White, r * 0.85f, Offset(cloudCenter.x, cloudCenter.y + r * 0.15f))

            val cMouthW = r * 0.5f
            val cMouthH = r * 0.35f
            drawArc(
                color = Color(0xFF78909C),
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cloudCenter.x - cMouthW / 2f, cloudCenter.y - r * 0.15f),
                size = Size(cMouthW, cMouthH),
                style = Stroke(width = maxOf(1.2f, radius * 0.018f), cap = StrokeCap.Round)
            )
        }

        drawCloud(Offset(center.x - radius * 0.44f, center.y + radius * 0.48f))
        drawCloud(Offset(center.x + radius * 0.44f, center.y + radius * 0.48f))

        // Sparkles
        fun drawSparkle(sparkleCenter: Offset, sparkleSize: Float) {
            val sparkleColor = Color(0xFFFFD700)
            val strokeW = maxOf(1.5f, sparkleSize * 0.22f)
            drawLine(sparkleColor, Offset(sparkleCenter.x - sparkleSize, sparkleCenter.y), Offset(sparkleCenter.x + sparkleSize, sparkleCenter.y), strokeW, StrokeCap.Round)
            drawLine(sparkleColor, Offset(sparkleCenter.x, sparkleCenter.y - sparkleSize), Offset(sparkleCenter.x, sparkleCenter.y + sparkleSize), strokeW, StrokeCap.Round)
            drawCircle(Color.White, strokeW * 0.7f, sparkleCenter)
        }

        drawSparkle(Offset(center.x + radius * 0.65f, center.y - radius * 0.60f), radius * 0.14f)
        drawSparkle(Offset(center.x - radius * 0.62f, center.y - radius * 0.48f), radius * 0.10f)
    }

    override fun drawToCanvas(canvas: AndroidCanvas, cx: Float, cy: Float, size: Float) {
        val radius = size / 2f
        val bandWidth = (radius * 0.44f) / 7f

        val fillPaint = AndroidPaint().apply { isAntiAlias = true; style = AndroidPaint.Style.FILL }
        val strokePaint = AndroidPaint().apply { isAntiAlias = true; style = AndroidPaint.Style.STROKE; strokeCap = AndroidPaint.Cap.ROUND }

        fillPaint.color = 0x10000000
        canvas.drawCircle(cx, cy, radius * 0.98f, fillPaint)

        val innerRadius = radius * 0.50f
        fillPaint.color = 0xFFF0F8FF.toInt()
        canvas.drawCircle(cx, cy, innerRadius, fillPaint)

        val outerR = radius * 0.94f
        strokePaint.strokeWidth = bandWidth + 0.6f
        for (i in RainbowStickerColors.AndroidColors.indices) {
            val r = outerR - (i * bandWidth) - (bandWidth / 2f)
            strokePaint.color = RainbowStickerColors.AndroidColors[i]
            canvas.drawCircle(cx, cy, r, strokePaint)
        }

        val sunRadius = innerRadius * 0.42f
        fillPaint.color = 0x33FFD700
        canvas.drawCircle(cx, cy, sunRadius * 1.35f, fillPaint)
        fillPaint.color = 0xFFFFD700.toInt()
        canvas.drawCircle(cx, cy, sunRadius, fillPaint)
        fillPaint.color = 0xFFFFE600.toInt()
        canvas.drawCircle(cx, cy, sunRadius * 0.72f, fillPaint)

        val eyeRadius = radius * 0.024f
        val eyeOffsetY = radius * 0.035f
        val eyeOffsetX = radius * 0.065f
        fillPaint.color = 0xFF5D4037.toInt()
        canvas.drawCircle(cx - eyeOffsetX, cy - eyeOffsetY, eyeRadius, fillPaint)
        canvas.drawCircle(cx + eyeOffsetX, cy - eyeOffsetY, eyeRadius, fillPaint)

        val mouthWidth = radius * 0.09f
        val mouthHeight = radius * 0.05f
        val mouthRect = RectF(cx - mouthWidth / 2f, cy - radius * 0.01f, cx + mouthWidth / 2f, cy - radius * 0.01f + mouthHeight)
        strokePaint.color = 0xFF5D4037.toInt()
        strokePaint.strokeWidth = maxOf(1.5f, radius * 0.02f)
        canvas.drawArc(mouthRect, 10f, 160f, false, strokePaint)

        val cheekRadius = radius * 0.025f
        fillPaint.color = 0x66FF4081.toInt()
        canvas.drawCircle(cx - eyeOffsetX - eyeRadius * 1.2f, cy, cheekRadius, fillPaint)
        canvas.drawCircle(cx + eyeOffsetX + eyeRadius * 1.2f, cy, cheekRadius, fillPaint)

        fun drawCloudToCanvas(cloudX: Float, cloudY: Float) {
            val r = radius * 0.16f
            fillPaint.color = 0x18000000
            canvas.drawCircle(cloudX - r * 0.85f, cloudY + r * 0.15f, r * 1.05f, fillPaint)
            canvas.drawCircle(cloudX, cloudY - r * 0.35f, r * 1.35f, fillPaint)
            canvas.drawCircle(cloudX + r * 0.85f, cloudY + r * 0.15f, r * 1.05f, fillPaint)

            fillPaint.color = AndroidColor.WHITE
            canvas.drawCircle(cloudX - r * 0.85f, cloudY + r * 0.05f, r * 0.95f, fillPaint)
            canvas.drawCircle(cloudX, cloudY - r * 0.38f, r * 1.30f, fillPaint)
            canvas.drawCircle(cloudX + r * 0.85f, cloudY + r * 0.05f, r * 0.95f, fillPaint)
            canvas.drawCircle(cloudX, cloudY + r * 0.15f, r * 0.85f, fillPaint)

            val cMouthW = r * 0.5f
            val cMouthH = r * 0.35f
            val cMouthRect = RectF(cloudX - cMouthW / 2f, cloudY - r * 0.15f, cloudX + cMouthW / 2f, cloudY - r * 0.15f + cMouthH)
            strokePaint.color = 0xFF78909C.toInt()
            strokePaint.strokeWidth = maxOf(1.2f, radius * 0.018f)
            canvas.drawArc(cMouthRect, 10f, 160f, false, strokePaint)
        }

        drawCloudToCanvas(cx - radius * 0.44f, cy + radius * 0.48f)
        drawCloudToCanvas(cx + radius * 0.44f, cy + radius * 0.48f)

        fun drawSparkleToCanvas(spX: Float, spY: Float, spSize: Float) {
            strokePaint.color = 0xFFFFD700.toInt()
            val strokeW = maxOf(1.5f, spSize * 0.22f)
            strokePaint.strokeWidth = strokeW
            canvas.drawLine(spX - spSize, spY, spX + spSize, spY, strokePaint)
            canvas.drawLine(spX, spY - spSize, spX, spY + spSize, strokePaint)
            fillPaint.color = AndroidColor.WHITE
            canvas.drawCircle(spX, spY, strokeW * 0.7f, fillPaint)
        }

        drawSparkleToCanvas(cx + radius * 0.65f, cy - radius * 0.60f, radius * 0.14f)
        drawSparkleToCanvas(cx - radius * 0.62f, cy - radius * 0.48f, radius * 0.10f)
    }

    @Composable
    override fun Icon(modifier: Modifier) {
        Canvas(modifier = modifier) {
            val sizeMin = minOf(size.width, size.height)
            draw(Offset(size.width / 2f, size.height / 2f), sizeMin)
        }
    }
}

/**
 * 180° Symmetrical Rainbow Arch with smiling cloud bases and sparkles.
 */
object RainbowArchSticker : CustomSticker {
    override val id: String = "🌈"
    override val contentDescription: String = "Rainbow arch stamp"

    override fun DrawScope.draw(center: Offset, size: Float) {
        val radius = size / 2f
        val archCenter = Offset(center.x, center.y + radius * 0.28f)
        val outerR = radius * 0.88f
        val bandSpan = radius * 0.46f
        val bandWidth = bandSpan / 7f

        val shadowWidth = bandWidth + 2f
        drawArc(
            color = Color(0x12000000),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(archCenter.x - outerR - 1.5f, archCenter.y - outerR - 1.5f),
            size = Size((outerR + 1.5f) * 2f, (outerR + 1.5f) * 2f),
            style = Stroke(width = shadowWidth, cap = StrokeCap.Round)
        )

        for (i in RainbowStickerColors.ComposeColors.indices) {
            val r = outerR - (i * bandWidth) - (bandWidth / 2f)
            drawArc(
                color = RainbowStickerColors.ComposeColors[i],
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(archCenter.x - r, archCenter.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = bandWidth + 0.8f, cap = StrokeCap.Butt)
            )
        }

        val midBandR = outerR - (bandSpan / 2f)
        fun drawCloud(cloudCenter: Offset) {
            val cr = radius * 0.18f
            val shadowColor = Color(0x18000000)

            drawCircle(shadowColor, cr * 0.95f, Offset(cloudCenter.x - cr * 0.85f, cloudCenter.y + cr * 0.20f))
            drawCircle(shadowColor, cr * 1.30f, Offset(cloudCenter.x, cloudCenter.y - cr * 0.22f))
            drawCircle(shadowColor, cr * 0.95f, Offset(cloudCenter.x + cr * 0.85f, cloudCenter.y + cr * 0.20f))

            drawCircle(Color.White, cr * 0.90f, Offset(cloudCenter.x - cr * 0.85f, cloudCenter.y + cr * 0.08f))
            drawCircle(Color.White, cr * 1.25f, Offset(cloudCenter.x, cloudCenter.y - cr * 0.26f))
            drawCircle(Color.White, cr * 0.90f, Offset(cloudCenter.x + cr * 0.85f, cloudCenter.y + cr * 0.08f))
            drawCircle(Color.White, cr * 0.80f, Offset(cloudCenter.x, cloudCenter.y + cr * 0.18f))

            val eyeR = cr * 0.13f
            val eyeColor = Color(0xFF5D4037)
            drawCircle(eyeColor, eyeR, Offset(cloudCenter.x - cr * 0.32f, cloudCenter.y - cr * 0.12f))
            drawCircle(eyeColor, eyeR, Offset(cloudCenter.x + cr * 0.32f, cloudCenter.y - cr * 0.12f))

            val cheekR = cr * 0.16f
            val cheekColor = Color(0x55FF4081)
            drawCircle(cheekColor, cheekR, Offset(cloudCenter.x - cr * 0.54f, cloudCenter.y + cr * 0.02f))
            drawCircle(cheekColor, cheekR, Offset(cloudCenter.x + cr * 0.54f, cloudCenter.y + cr * 0.02f))

            val cMouthW = cr * 0.44f
            val cMouthH = cr * 0.28f
            drawArc(
                color = eyeColor,
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(cloudCenter.x - cMouthW / 2f, cloudCenter.y - cr * 0.04f),
                size = Size(cMouthW, cMouthH),
                style = Stroke(width = maxOf(1.2f, radius * 0.018f), cap = StrokeCap.Round)
            )
        }

        val cloudY = archCenter.y + radius * 0.08f
        drawCloud(Offset(center.x - midBandR, cloudY))
        drawCloud(Offset(center.x + midBandR, cloudY))

        fun drawSparkle(sparkleCenter: Offset, sparkleSize: Float) {
            val sparkleColor = Color(0xFFFFD700)
            val strokeW = maxOf(1.5f, sparkleSize * 0.22f)
            drawLine(sparkleColor, Offset(sparkleCenter.x - sparkleSize, sparkleCenter.y), Offset(sparkleCenter.x + sparkleSize, sparkleCenter.y), strokeW, StrokeCap.Round)
            drawLine(sparkleColor, Offset(sparkleCenter.x, sparkleCenter.y - sparkleSize), Offset(sparkleCenter.x, sparkleCenter.y + sparkleSize), strokeW, StrokeCap.Round)
            drawCircle(Color.White, strokeW * 0.7f, sparkleCenter)
        }

        drawSparkle(Offset(center.x - radius * 0.55f, center.y - radius * 0.40f), radius * 0.13f)
        drawSparkle(Offset(center.x + radius * 0.58f, center.y - radius * 0.32f), radius * 0.15f)
        drawSparkle(Offset(center.x + radius * 0.06f, center.y - radius * 0.72f), radius * 0.11f)
    }

    override fun drawToCanvas(canvas: AndroidCanvas, cx: Float, cy: Float, size: Float) {
        val radius = size / 2f
        val archCenterX = cx
        val archCenterY = cy + radius * 0.28f
        val outerR = radius * 0.88f
        val bandSpan = radius * 0.46f
        val bandWidth = bandSpan / 7f

        val fillPaint = AndroidPaint().apply { isAntiAlias = true; style = AndroidPaint.Style.FILL }
        val strokePaint = AndroidPaint().apply { isAntiAlias = true; style = AndroidPaint.Style.STROKE }

        val shadowWidth = bandWidth + 2f
        strokePaint.strokeWidth = shadowWidth
        strokePaint.strokeCap = AndroidPaint.Cap.ROUND
        strokePaint.color = 0x12000000
        val shadowRect = RectF(archCenterX - outerR - 1.5f, archCenterY - outerR - 1.5f, archCenterX + outerR + 1.5f, archCenterY + outerR + 1.5f)
        canvas.drawArc(shadowRect, 180f, 180f, false, strokePaint)

        strokePaint.strokeWidth = bandWidth + 0.8f
        strokePaint.strokeCap = AndroidPaint.Cap.BUTT
        for (i in RainbowStickerColors.AndroidColors.indices) {
            val r = outerR - (i * bandWidth) - (bandWidth / 2f)
            strokePaint.color = RainbowStickerColors.AndroidColors[i]
            val bandRect = RectF(archCenterX - r, archCenterY - r, archCenterX + r, archCenterY + r)
            canvas.drawArc(bandRect, 180f, 180f, false, strokePaint)
        }

        val midBandR = outerR - (bandSpan / 2f)
        fun drawCloudToCanvas(cloudX: Float, cloudY: Float) {
            val cr = radius * 0.18f
            fillPaint.color = 0x18000000
            canvas.drawCircle(cloudX - cr * 0.85f, cloudY + cr * 0.20f, cr * 0.95f, fillPaint)
            canvas.drawCircle(cloudX, cloudY - cr * 0.22f, cr * 1.30f, fillPaint)
            canvas.drawCircle(cloudX + cr * 0.85f, cloudY + cr * 0.20f, cr * 0.95f, fillPaint)

            fillPaint.color = AndroidColor.WHITE
            canvas.drawCircle(cloudX - cr * 0.85f, cloudY + cr * 0.08f, cr * 0.90f, fillPaint)
            canvas.drawCircle(cloudX, cloudY - cr * 0.26f, cr * 1.25f, fillPaint)
            canvas.drawCircle(cloudX + cr * 0.85f, cloudY + cr * 0.08f, cr * 0.90f, fillPaint)
            canvas.drawCircle(cloudX, cloudY + cr * 0.18f, cr * 0.80f, fillPaint)

            val eyeR = cr * 0.13f
            fillPaint.color = 0xFF5D4037.toInt()
            canvas.drawCircle(cloudX - cr * 0.32f, cloudY - cr * 0.12f, eyeR, fillPaint)
            canvas.drawCircle(cloudX + cr * 0.32f, cloudY - cr * 0.12f, eyeR, fillPaint)

            val cheekR = cr * 0.16f
            fillPaint.color = 0x55FF4081.toInt()
            canvas.drawCircle(cloudX - cr * 0.54f, cloudY + cr * 0.02f, cheekR, fillPaint)
            canvas.drawCircle(cloudX + cr * 0.54f, cloudY + cr * 0.02f, cheekR, fillPaint)

            val cMouthW = cr * 0.44f
            val cMouthH = cr * 0.28f
            val cMouthRect = RectF(cloudX - cMouthW / 2f, cloudY - cr * 0.04f, cloudX + cMouthW / 2f, cloudY - cr * 0.04f + cMouthH)
            strokePaint.color = 0xFF5D4037.toInt()
            strokePaint.strokeWidth = maxOf(1.2f, radius * 0.018f)
            strokePaint.strokeCap = AndroidPaint.Cap.ROUND
            canvas.drawArc(cMouthRect, 10f, 160f, false, strokePaint)
        }

        val cloudY = archCenterY + radius * 0.08f
        drawCloudToCanvas(cx - midBandR, cloudY)
        drawCloudToCanvas(cx + midBandR, cloudY)

        fun drawSparkleToCanvas(spX: Float, spY: Float, spSize: Float) {
            strokePaint.color = 0xFFFFD700.toInt()
            val strokeW = maxOf(1.5f, spSize * 0.22f)
            strokePaint.strokeWidth = strokeW
            strokePaint.strokeCap = AndroidPaint.Cap.ROUND
            canvas.drawLine(spX - spSize, spY, spX + spSize, spY, strokePaint)
            canvas.drawLine(spX, spY - spSize, spX, spY + spSize, strokePaint)
            fillPaint.color = AndroidColor.WHITE
            canvas.drawCircle(spX, spY, strokeW * 0.7f, fillPaint)
        }

        drawSparkleToCanvas(cx - radius * 0.55f, cy - radius * 0.40f, radius * 0.13f)
        drawSparkleToCanvas(cx + radius * 0.58f, cy - radius * 0.32f, radius * 0.15f)
        drawSparkleToCanvas(cx + radius * 0.06f, cy - radius * 0.72f, radius * 0.11f)
    }

    @Composable
    override fun Icon(modifier: Modifier) {
        Canvas(modifier = modifier) {
            val sizeMin = minOf(size.width, size.height)
            draw(Offset(size.width / 2f, size.height / 2f), sizeMin)
        }
    }
}
