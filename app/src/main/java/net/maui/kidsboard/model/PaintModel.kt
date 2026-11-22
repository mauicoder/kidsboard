package net.maui.kidsboard.model

import android.graphics.Color
import android.graphics.Paint

class PaintModel internal constructor(
    private val paintFactory: () -> Paint = { Paint(Paint.ANTI_ALIAS_FLAG) }
) {
    var color: Int = Color.BLACK
        private set

    var strokeWidth: Float = DEFAULT_STROKE_WIDTH
        private set

    private var _paint: Paint? = null
    val paint: Paint
        get() {
            val p = _paint ?: paintFactory().also { _paint = it }
            p.style = Paint.Style.STROKE
            p.strokeWidth = strokeWidth
            p.color = color
            return p
        }

    companion object {
        const val DEFAULT_STROKE_WIDTH = 5f
        const val ERASER_STROKE_WIDTH = 15f

        @JvmStatic
        val instance: PaintModel by lazy { PaintModel() }

        fun createForTesting(paintFactory: () -> Paint): PaintModel = PaintModel(paintFactory)
    }

    fun setDefaultPaint(): Paint {
        color = Color.BLACK
        strokeWidth = DEFAULT_STROKE_WIDTH
        return paint
    }

    fun setColor(newColor: Int) {
        color = newColor
        strokeWidth = if (newColor == Color.WHITE) ERASER_STROKE_WIDTH else DEFAULT_STROKE_WIDTH
        _paint?.let {
            it.color = newColor
            it.strokeWidth = strokeWidth
        }
    }
}
