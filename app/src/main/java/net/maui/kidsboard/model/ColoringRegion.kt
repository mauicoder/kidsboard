package net.maui.kidsboard.model

import android.graphics.Path as AndroidPath
import android.graphics.RectF as AndroidRectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath

data class ColoringRegion(
    val id: Int,
    val name: String,
    val element: OutlineElement,
    val priority: Int = 0 // Higher priority is tested first
) {
    fun contains(normX: Float, normY: Float): Boolean {
        return when (element) {
            is OutlineElement.Circle -> {
                val dx = normX - element.cx
                val dy = normY - element.cy
                (dx * dx + dy * dy) <= (element.radius * element.radius)
            }
            is OutlineElement.Oval -> {
                val dx = (normX - element.cx) / element.rx
                val dy = (normY - element.cy) / element.ry
                (dx * dx + dy * dy) <= 1.0f
            }
            is OutlineElement.Path -> {
                if (!element.isClosed) return false
                val polygon = getPolygonForPath(element.actions)
                isPointInPolygon(normX, normY, polygon)
            }
        }
    }

    fun toComposePath(width: Float, height: Float): Path {
        val path = Path()
        when (element) {
            is OutlineElement.Circle -> {
                val cx = element.cx * width
                val cy = element.cy * height
                val r = element.radius * width
                path.addOval(Rect(cx - r, cy - r, cx + r, cy + r))
            }
            is OutlineElement.Oval -> {
                val cx = element.cx * width
                val cy = element.cy * height
                val rx = element.rx * width
                val ry = element.ry * height
                path.addOval(Rect(cx - rx, cy - ry, cx + rx, cy + ry))
            }
            is OutlineElement.Path -> {
                buildActionsPath(path, element.actions, width, height, element.isClosed)
            }
        }
        return path
    }

    fun toAndroidPath(width: Float, height: Float): AndroidPath {
        val path = AndroidPath()
        val rect = AndroidRectF()
        when (element) {
            is OutlineElement.Circle -> {
                val cx = element.cx * width
                val cy = element.cy * height
                val r = element.radius * width
                rect.set(cx - r, cy - r, cx + r, cy + r)
                path.addOval(rect, AndroidPath.Direction.CW)
            }
            is OutlineElement.Oval -> {
                val cx = element.cx * width
                val cy = element.cy * height
                val rx = element.rx * width
                val ry = element.ry * height
                rect.set(cx - rx, cy - ry, cx + rx, cy + ry)
                path.addOval(rect, AndroidPath.Direction.CW)
            }
            is OutlineElement.Path -> {
                buildAndroidActionsPath(path, element.actions, width, height, element.isClosed)
            }
        }
        return path
    }

    companion object {
        const val OUTSIDE_REGION_ID = -1

        private fun buildActionsPath(
            path: Path,
            actions: List<PathAction>,
            width: Float,
            height: Float,
            isClosed: Boolean
        ) {
            for (action in actions) {
                when (action) {
                    is PathAction.MoveTo -> path.moveTo(action.x * width, action.y * height)
                    is PathAction.LineTo -> path.lineTo(action.x * width, action.y * height)
                    is PathAction.QuadTo -> path.quadraticTo(
                        action.cx * width, action.cy * height,
                        action.x * width, action.y * height
                    )
                    is PathAction.CubicTo -> path.cubicTo(
                        action.c1x * width, action.c1y * height,
                        action.c2x * width, action.c2y * height,
                        action.x * width, action.y * height
                    )
                }
            }
            if (isClosed) path.close()
        }

        private fun buildAndroidActionsPath(
            path: AndroidPath,
            actions: List<PathAction>,
            width: Float,
            height: Float,
            isClosed: Boolean
        ) {
            for (action in actions) {
                when (action) {
                    is PathAction.MoveTo -> path.moveTo(action.x * width, action.y * height)
                    is PathAction.LineTo -> path.lineTo(action.x * width, action.y * height)
                    is PathAction.QuadTo -> path.quadTo(
                        action.cx * width, action.cy * height,
                        action.x * width, action.y * height
                    )
                    is PathAction.CubicTo -> path.cubicTo(
                        action.c1x * width, action.c1y * height,
                        action.c2x * width, action.c2y * height,
                        action.x * width, action.y * height
                    )
                }
            }
            if (isClosed) path.close()
        }

        private fun getPolygonForPath(actions: List<PathAction>): List<Pair<Float, Float>> {
            val points = mutableListOf<Pair<Float, Float>>()
            var currentX = 0f
            var currentY = 0f

            for (action in actions) {
                when (action) {
                    is PathAction.MoveTo -> {
                        currentX = action.x
                        currentY = action.y
                        points.add(currentX to currentY)
                    }
                    is PathAction.LineTo -> {
                        currentX = action.x
                        currentY = action.y
                        points.add(currentX to currentY)
                    }
                    is PathAction.QuadTo -> {
                        val steps = 4
                        for (i in 1..steps) {
                            val t = i.toFloat() / steps
                            val inv = 1f - t
                            val x = inv * inv * currentX + 2f * inv * t * action.cx + t * t * action.x
                            val y = inv * inv * currentY + 2f * inv * t * action.cy + t * t * action.y
                            points.add(x to y)
                        }
                        currentX = action.x
                        currentY = action.y
                    }
                    is PathAction.CubicTo -> {
                        val steps = 5
                        for (i in 1..steps) {
                            val t = i.toFloat() / steps
                            val inv = 1f - t
                            val x = inv * inv * inv * currentX +
                                3f * inv * inv * t * action.c1x +
                                3f * inv * t * t * action.c2x +
                                t * t * t * action.x
                            val y = inv * inv * inv * currentY +
                                3f * inv * inv * t * action.c1y +
                                3f * inv * t * t * action.c2y +
                                t * t * t * action.y
                            points.add(x to y)
                        }
                        currentX = action.x
                        currentY = action.y
                    }
                }
            }
            return points
        }

        fun isPointInPolygon(px: Float, py: Float, polygon: List<Pair<Float, Float>>): Boolean {
            if (polygon.size < 3) return false
            var inside = false
            var j = polygon.size - 1
            for (i in polygon.indices) {
                val (xi, yi) = polygon[i]
                val (xj, yj) = polygon[j]
                if ((yi > py) != (yj > py) &&
                    (px < (xj - xi) * (py - yi) / (yj - yi) + xi)
                ) {
                    inside = !inside
                }
                j = i
            }
            return inside
        }
    }
}
