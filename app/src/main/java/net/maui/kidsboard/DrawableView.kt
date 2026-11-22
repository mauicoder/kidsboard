package net.maui.kidsboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import net.maui.kidsboard.model.PaintModel

class DrawableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var oldX: Float = 0f
    private var oldY: Float = 0f
    private var curX: Float = -1f
    private var curY: Float = -1f
    private var draw: Boolean = false
    private val pm: PaintModel = PaintModel.instance

    private var bitmap: Bitmap? = null
    private var bufferCanvas: Canvas? = null
    private var boundsRect: Rect = Rect()

    init {
        setBackgroundColor(Color.WHITE)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            bitmap?.recycle()
            val newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(newBitmap)
            val rect = Rect(0, 0, w, h)
            canvas.drawRect(rect, pm.paint)

            bitmap = newBitmap
            bufferCanvas = canvas
            boundsRect = rect
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bCanvas = bufferCanvas
        val bmp = bitmap
        if (draw && bCanvas != null) {
            bCanvas.drawLine(oldX, oldY, curX, curY, pm.paint)
            storePosition()
        }
        if (bmp != null) {
            canvas.drawBitmap(bmp, boundsRect, boundsRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        curX = event.x
        curY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                storePosition()
                draw = false
            }
            MotionEvent.ACTION_MOVE -> {
                draw = true
            }
            MotionEvent.ACTION_UP -> {
                draw = false
            }
        }
        invalidate()
        return true
    }

    private fun storePosition() {
        oldX = curX
        oldY = curY
    }
}
