package dev.mauicoder.kidsboard.model

sealed interface PathAction {
    data class MoveTo(val x: Float, val y: Float) : PathAction
    data class LineTo(val x: Float, val y: Float) : PathAction
    data class QuadTo(val cx: Float, val cy: Float, val x: Float, val y: Float) : PathAction
    data class CubicTo(
        val c1x: Float,
        val c1y: Float,
        val c2x: Float,
        val c2y: Float,
        val x: Float,
        val y: Float
    ) : PathAction
}

sealed interface OutlineElement {
    data class Circle(
        val cx: Float,
        val cy: Float,
        val radius: Float,
        val isFilled: Boolean = false
    ) : OutlineElement

    data class Oval(
        val cx: Float,
        val cy: Float,
        val rx: Float,
        val ry: Float,
        val isFilled: Boolean = false
    ) : OutlineElement

    data class Path(
        val actions: List<PathAction>,
        val isClosed: Boolean = false,
        val isFilled: Boolean = false
    ) : OutlineElement
}

object ColoringOutlineTemplates {

    fun getTemplate(background: CanvasBackground): List<OutlineElement> {
        return when (background) {
            CanvasBackground.CuteBear -> cuteBearElements
            CanvasBackground.RocketShip -> rocketShipElements
            CanvasBackground.SunAndCloud -> sunAndCloudElements
            CanvasBackground.RaceCar -> raceCarElements
            CanvasBackground.FlowerButterfly -> flowerButterflyElements
            else -> emptyList()
        }
    }

    // 1. Cute Teddy Bear
    val cuteBearElements: List<OutlineElement> = listOf(
        // Outer Ears
        OutlineElement.Circle(cx = 0.33f, cy = 0.24f, radius = 0.085f),
        OutlineElement.Circle(cx = 0.67f, cy = 0.24f, radius = 0.085f),
        // Inner Ears
        OutlineElement.Circle(cx = 0.33f, cy = 0.24f, radius = 0.045f),
        OutlineElement.Circle(cx = 0.67f, cy = 0.24f, radius = 0.045f),
        // Body
        OutlineElement.Oval(cx = 0.50f, cy = 0.70f, rx = 0.23f, ry = 0.21f),
        // Belly Patch
        OutlineElement.Oval(cx = 0.50f, cy = 0.72f, rx = 0.14f, ry = 0.13f),
        // Left & Right Arms
        OutlineElement.Oval(cx = 0.25f, cy = 0.63f, rx = 0.07f, ry = 0.13f),
        OutlineElement.Oval(cx = 0.75f, cy = 0.63f, rx = 0.07f, ry = 0.13f),
        // Feet
        OutlineElement.Oval(cx = 0.35f, cy = 0.88f, rx = 0.09f, ry = 0.06f),
        OutlineElement.Oval(cx = 0.65f, cy = 0.88f, rx = 0.09f, ry = 0.06f),
        // Head
        OutlineElement.Circle(cx = 0.50f, cy = 0.42f, radius = 0.21f),
        // Snout Muzzle
        OutlineElement.Oval(cx = 0.50f, cy = 0.48f, rx = 0.085f, ry = 0.060f),
        // Nose (Filled)
        OutlineElement.Oval(cx = 0.50f, cy = 0.45f, rx = 0.032f, ry = 0.022f, isFilled = true),
        // Eyes (Filled)
        OutlineElement.Circle(cx = 0.42f, cy = 0.38f, radius = 0.020f, isFilled = true),
        OutlineElement.Circle(cx = 0.58f, cy = 0.38f, radius = 0.020f, isFilled = true),
        // Smiling Mouth
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.50f, 0.472f),
                PathAction.LineTo(0.50f, 0.500f),
                PathAction.QuadTo(0.46f, 0.525f, 0.44f, 0.505f)
            ),
            isClosed = false
        ),
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.50f, 0.500f),
                PathAction.QuadTo(0.54f, 0.525f, 0.56f, 0.505f)
            ),
            isClosed = false
        )
    )

    // 2. Rocket Ship in Space
    val rocketShipElements: List<OutlineElement> = listOf(
        // Left & Right Rocket Fins
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.38f, 0.50f),
                PathAction.LineTo(0.22f, 0.67f),
                PathAction.QuadTo(0.30f, 0.67f, 0.38f, 0.64f)
            ),
            isClosed = true
        ),
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.62f, 0.50f),
                PathAction.LineTo(0.78f, 0.67f),
                PathAction.QuadTo(0.70f, 0.67f, 0.62f, 0.64f)
            ),
            isClosed = true
        ),
        // Rocket Fuselage Capsule
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.50f, 0.12f),
                PathAction.CubicTo(0.43f, 0.22f, 0.38f, 0.42f, 0.38f, 0.65f),
                PathAction.LineTo(0.62f, 0.65f),
                PathAction.CubicTo(0.62f, 0.42f, 0.57f, 0.22f, 0.50f, 0.12f)
            ),
            isClosed = true
        ),
        // Nose Cone separator line
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.42f, 0.25f),
                PathAction.QuadTo(0.50f, 0.28f, 0.58f, 0.25f)
            ),
            isClosed = false
        ),
        // Porthole Window
        OutlineElement.Circle(cx = 0.50f, cy = 0.40f, radius = 0.080f),
        OutlineElement.Circle(cx = 0.50f, cy = 0.40f, radius = 0.055f),
        // Thruster Nozzle
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.44f, 0.65f),
                PathAction.LineTo(0.41f, 0.72f),
                PathAction.LineTo(0.59f, 0.72f),
                PathAction.LineTo(0.56f, 0.65f)
            ),
            isClosed = true
        ),
        // Rocket Fire Exhaust
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.43f, 0.72f),
                PathAction.LineTo(0.40f, 0.81f),
                PathAction.LineTo(0.47f, 0.78f),
                PathAction.LineTo(0.50f, 0.87f),
                PathAction.LineTo(0.53f, 0.78f),
                PathAction.LineTo(0.60f, 0.81f),
                PathAction.LineTo(0.57f, 0.72f)
            ),
            isClosed = true
        ),
        // Distant Stars
        starElement(cx = 0.20f, cy = 0.22f, size = 0.045f),
        starElement(cx = 0.80f, cy = 0.20f, size = 0.045f),
        starElement(cx = 0.18f, cy = 0.78f, size = 0.035f),
        // Planet with Ring
        OutlineElement.Circle(cx = 0.80f, cy = 0.75f, radius = 0.055f),
        OutlineElement.Oval(cx = 0.80f, cy = 0.75f, rx = 0.090f, ry = 0.025f)
    )

    // 3. Happy Sun and Cloud
    val sunAndCloudElements: List<OutlineElement> = listOf(
        // Sun Center
        OutlineElement.Circle(cx = 0.45f, cy = 0.36f, radius = 0.17f),
        // Sun Eyes
        OutlineElement.Circle(cx = 0.40f, cy = 0.33f, radius = 0.018f, isFilled = true),
        OutlineElement.Circle(cx = 0.50f, cy = 0.33f, radius = 0.018f, isFilled = true),
        // Sun Smile
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.38f, 0.39f),
                PathAction.QuadTo(0.45f, 0.45f, 0.52f, 0.39f)
            ),
            isClosed = false
        ),
        // Sun Rays (8 rays)
        sunRay(0.45f, 0.14f, 0.45f, 0.19f),
        sunRay(0.45f, 0.53f, 0.45f, 0.58f),
        sunRay(0.23f, 0.36f, 0.28f, 0.36f),
        sunRay(0.62f, 0.36f, 0.67f, 0.36f),
        sunRay(0.30f, 0.21f, 0.34f, 0.25f),
        sunRay(0.60f, 0.21f, 0.56f, 0.25f),
        sunRay(0.29f, 0.51f, 0.33f, 0.47f),
        sunRay(0.61f, 0.51f, 0.57f, 0.47f),
        // Fluffy Cloud (Overlapping rounded bumps)
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.24f, 0.76f),
                // Left puff
                PathAction.CubicTo(0.16f, 0.76f, 0.16f, 0.64f, 0.26f, 0.62f),
                // Top-left puff
                PathAction.CubicTo(0.28f, 0.52f, 0.42f, 0.50f, 0.48f, 0.57f),
                // Top-right puff
                PathAction.CubicTo(0.53f, 0.48f, 0.72f, 0.50f, 0.74f, 0.60f),
                // Right puff
                PathAction.CubicTo(0.85f, 0.62f, 0.85f, 0.76f, 0.76f, 0.76f)
            ),
            isClosed = true
        ),
        // Cloud Smile and Eyes
        OutlineElement.Circle(cx = 0.46f, cy = 0.65f, radius = 0.014f, isFilled = true),
        OutlineElement.Circle(cx = 0.54f, cy = 0.65f, radius = 0.014f, isFilled = true),
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.47f, 0.69f),
                PathAction.QuadTo(0.50f, 0.73f, 0.53f, 0.69f)
            ),
            isClosed = false
        ),
        // Little raindrops below cloud
        OutlineElement.Oval(cx = 0.32f, cy = 0.84f, rx = 0.012f, ry = 0.022f, isFilled = true),
        OutlineElement.Oval(cx = 0.50f, cy = 0.84f, rx = 0.012f, ry = 0.022f, isFilled = true),
        OutlineElement.Oval(cx = 0.68f, cy = 0.84f, rx = 0.012f, ry = 0.022f, isFilled = true)
    )

    // 4. Cool Cartoon Race Car
    val raceCarElements: List<OutlineElement> = listOf(
        // Rear Spoiler
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.12f, 0.46f),
                PathAction.LineTo(0.24f, 0.46f),
                PathAction.LineTo(0.23f, 0.50f),
                PathAction.LineTo(0.12f, 0.50f)
            ),
            isClosed = true
        ),
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.17f, 0.50f),
                PathAction.LineTo(0.17f, 0.58f),
                PathAction.MoveTo(0.20f, 0.50f),
                PathAction.LineTo(0.20f, 0.58f)
            ),
            isClosed = false
        ),
        // Car Body Silhouette
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.14f, 0.65f),
                // Rear fender
                PathAction.LineTo(0.14f, 0.58f),
                PathAction.LineTo(0.28f, 0.58f),
                // Cockpit slope up
                PathAction.LineTo(0.38f, 0.44f),
                PathAction.LineTo(0.58f, 0.44f),
                // Windshield slope down to hood
                PathAction.LineTo(0.68f, 0.56f),
                // Hood & Front bumper
                PathAction.LineTo(0.86f, 0.58f),
                PathAction.QuadTo(0.88f, 0.65f, 0.85f, 0.67f),
                // Underside to right wheel arch
                PathAction.LineTo(0.77f, 0.67f),
                PathAction.QuadTo(0.68f, 0.58f, 0.59f, 0.67f),
                // Underbody between wheels
                PathAction.LineTo(0.41f, 0.67f),
                PathAction.QuadTo(0.32f, 0.58f, 0.23f, 0.67f),
                PathAction.LineTo(0.14f, 0.67f)
            ),
            isClosed = true
        ),
        // Windshield & Side Windows
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.41f, 0.46f),
                PathAction.LineTo(0.56f, 0.46f),
                PathAction.LineTo(0.63f, 0.55f),
                PathAction.LineTo(0.41f, 0.55f)
            ),
            isClosed = true
        ),
        // Rear Wheel (Tire + Rim)
        OutlineElement.Circle(cx = 0.32f, cy = 0.67f, radius = 0.085f),
        OutlineElement.Circle(cx = 0.32f, cy = 0.67f, radius = 0.045f),
        // Front Wheel (Tire + Rim)
        OutlineElement.Circle(cx = 0.68f, cy = 0.67f, radius = 0.085f),
        OutlineElement.Circle(cx = 0.68f, cy = 0.67f, radius = 0.045f),
        // Star on Door
        starElement(cx = 0.50f, cy = 0.61f, size = 0.040f),
        // Headlight
        OutlineElement.Oval(cx = 0.84f, cy = 0.60f, rx = 0.020f, ry = 0.015f)
    )

    // 5. Flower & Butterfly
    val flowerButterflyElements: List<OutlineElement> = listOf(
        // Stem
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.42f, 0.58f),
                PathAction.QuadTo(0.40f, 0.72f, 0.44f, 0.88f)
            ),
            isClosed = false
        ),
        // Left Leaf
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.41f, 0.70f),
                PathAction.QuadTo(0.28f, 0.67f, 0.25f, 0.75f),
                PathAction.QuadTo(0.35f, 0.78f, 0.42f, 0.74f)
            ),
            isClosed = true
        ),
        // Right Leaf
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.42f, 0.76f),
                PathAction.QuadTo(0.54f, 0.72f, 0.58f, 0.80f),
                PathAction.QuadTo(0.49f, 0.84f, 0.43f, 0.80f)
            ),
            isClosed = true
        ),
        // Flower Petals (6 surrounding petals)
        OutlineElement.Circle(cx = 0.42f, cy = 0.34f, radius = 0.085f), // Top
        OutlineElement.Circle(cx = 0.42f, cy = 0.58f, radius = 0.085f), // Bottom
        OutlineElement.Circle(cx = 0.31f, cy = 0.40f, radius = 0.085f), // Top-Left
        OutlineElement.Circle(cx = 0.53f, cy = 0.40f, radius = 0.085f), // Top-Right
        OutlineElement.Circle(cx = 0.31f, cy = 0.52f, radius = 0.085f), // Bottom-Left
        OutlineElement.Circle(cx = 0.53f, cy = 0.52f, radius = 0.085f), // Bottom-Right
        // Flower Center
        OutlineElement.Circle(cx = 0.42f, cy = 0.46f, radius = 0.095f),
        // Flower Smile & Eyes
        OutlineElement.Circle(cx = 0.38f, cy = 0.44f, radius = 0.015f, isFilled = true),
        OutlineElement.Circle(cx = 0.46f, cy = 0.44f, radius = 0.015f, isFilled = true),
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.37f, 0.48f),
                PathAction.QuadTo(0.42f, 0.53f, 0.47f, 0.48f)
            ),
            isClosed = false
        ),
        // Butterfly Body
        OutlineElement.Oval(cx = 0.74f, cy = 0.28f, rx = 0.016f, ry = 0.050f, isFilled = true),
        // Butterfly Antennae
        OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(0.73f, 0.23f),
                PathAction.QuadTo(0.70f, 0.19f, 0.68f, 0.20f),
                PathAction.MoveTo(0.75f, 0.23f),
                PathAction.QuadTo(0.78f, 0.19f, 0.80f, 0.20f)
            ),
            isClosed = false
        ),
        // Butterfly Wings (Top & Bottom Left/Right)
        OutlineElement.Oval(cx = 0.67f, cy = 0.24f, rx = 0.055f, ry = 0.040f),
        OutlineElement.Oval(cx = 0.68f, cy = 0.31f, rx = 0.040f, ry = 0.030f),
        OutlineElement.Oval(cx = 0.81f, cy = 0.24f, rx = 0.055f, ry = 0.040f),
        OutlineElement.Oval(cx = 0.80f, cy = 0.31f, rx = 0.040f, ry = 0.030f)
    )

    private fun sunRay(x1: Float, y1: Float, x2: Float, y2: Float): OutlineElement {
        return OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(x1, y1),
                PathAction.LineTo(x2, y2)
            ),
            isClosed = false
        )
    }

    internal fun starElement(cx: Float, cy: Float, size: Float): OutlineElement {
        val half = size / 2f
        val quarter = size / 4f
        return OutlineElement.Path(
            actions = listOf(
                PathAction.MoveTo(cx, cy - half),
                PathAction.LineTo(cx + quarter, cy - quarter),
                PathAction.LineTo(cx + half, cy),
                PathAction.LineTo(cx + quarter, cy + quarter),
                PathAction.LineTo(cx, cy + half),
                PathAction.LineTo(cx - quarter, cy + quarter),
                PathAction.LineTo(cx - half, cy),
                PathAction.LineTo(cx - quarter, cy - quarter)
            ),
            isClosed = true
        )
    }
}
