package dev.mauicoder.kidsboard.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.ceil
import kotlin.math.hypot

/**
 * Shared utility for evaluating and subdividing continuous rainbow strokes along quadratic Bezier curves.
 * Eliminates duplicate subdivision math between interactive Compose canvas drawing and Android bitmap export.
 */
object RainbowStrokeGeometry {

    /**
     * Functional interface / listener for each generated sub-segment of a rainbow curve.
     */
    fun interface SegmentConsumer {
        fun onSegment(start: Offset, end: Offset, cumulativeDist: Float)
    }

    fun forEachSubdivision(
        points: List<Offset>,
        strokeWidth: Float,
        consumer: SegmentConsumer
    ) {
        if (points.size < 2) return

        val stepSize = maxOf(2f, minOf(6f, strokeWidth * 0.25f))
        var cumulativeDist = 0f
        var prevPoint = points[0]

        fun drawCurve(start: Offset, control: Offset, end: Offset) {
            val l1 = hypot(end.x - start.x, end.y - start.y)
            val l2 = hypot(control.x - start.x, control.y - start.y) +
                    hypot(end.x - control.x, end.y - control.y)
            val approxLength = (l1 + l2) / 2f
            val steps = maxOf(1, ceil(approxLength / stepSize).toInt())

            for (s in 1..steps) {
                val t = s.toFloat() / steps
                val omt = 1f - t
                val x = omt * omt * start.x + 2f * omt * t * control.x + t * t * end.x
                val y = omt * omt * start.y + 2f * omt * t * control.y + t * t * end.y
                val currPoint = Offset(x, y)

                val segDist = hypot(currPoint.x - prevPoint.x, currPoint.y - prevPoint.y)
                cumulativeDist += segDist
                consumer.onSegment(prevPoint, currPoint, cumulativeDist)
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
}
