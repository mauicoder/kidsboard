package dev.mauicoder.kidsboard.model

import android.graphics.Path as AndroidPath
import androidx.compose.ui.graphics.Path

object ColoringRegionManager {

    fun getRegions(background: CanvasBackground): List<ColoringRegion> {
        return when (background) {
            CanvasBackground.CuteBear -> cuteBearRegions
            CanvasBackground.RocketShip -> rocketShipRegions
            CanvasBackground.SunAndCloud -> sunAndCloudRegions
            CanvasBackground.RaceCar -> raceCarRegions
            CanvasBackground.FlowerButterfly -> flowerButterflyRegions
            else -> emptyList()
        }
    }

    fun findRegionAt(background: CanvasBackground, normX: Float, normY: Float): ColoringRegion? {
        val regions = getRegions(background)
        if (regions.isEmpty()) return null
        return regions
            .filter { it.contains(normX, normY) }
            .maxByOrNull { it.priority }
    }

    fun getRegionById(background: CanvasBackground, id: Int): ColoringRegion? {
        return getRegions(background).firstOrNull { it.id == id }
    }

    fun getCharacterUnionComposePath(background: CanvasBackground, width: Float, height: Float): Path {
        val path = Path()
        val regions = getRegions(background)
        for (region in regions) {
            path.addPath(region.toComposePath(width, height))
        }
        return path
    }

    fun getCharacterUnionAndroidPath(background: CanvasBackground, width: Float, height: Float): AndroidPath {
        val path = AndroidPath()
        val regions = getRegions(background)
        for (region in regions) {
            path.addPath(region.toAndroidPath(width, height))
        }
        return path
    }

    // --- 1. Cute Teddy Bear Regions ---
    val cuteBearRegions: List<ColoringRegion> by lazy {
        listOf(
            ColoringRegion(
                id = 101,
                name = "Left Inner Ear",
                element = OutlineElement.Circle(cx = 0.33f, cy = 0.24f, radius = 0.045f),
                priority = 10
            ),
            ColoringRegion(
                id = 102,
                name = "Right Inner Ear",
                element = OutlineElement.Circle(cx = 0.67f, cy = 0.24f, radius = 0.045f),
                priority = 10
            ),
            ColoringRegion(
                id = 103,
                name = "Left Outer Ear",
                element = OutlineElement.Circle(cx = 0.33f, cy = 0.24f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 104,
                name = "Right Outer Ear",
                element = OutlineElement.Circle(cx = 0.67f, cy = 0.24f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 105,
                name = "Snout Muzzle",
                element = OutlineElement.Oval(cx = 0.50f, cy = 0.48f, rx = 0.085f, ry = 0.060f),
                priority = 10
            ),
            ColoringRegion(
                id = 106,
                name = "Nose",
                element = OutlineElement.Oval(cx = 0.50f, cy = 0.45f, rx = 0.032f, ry = 0.022f),
                priority = 15
            ),
            ColoringRegion(
                id = 107,
                name = "Head",
                element = OutlineElement.Circle(cx = 0.50f, cy = 0.42f, radius = 0.21f),
                priority = 1
            ),
            ColoringRegion(
                id = 108,
                name = "Belly Patch",
                element = OutlineElement.Oval(cx = 0.50f, cy = 0.72f, rx = 0.14f, ry = 0.13f),
                priority = 5
            ),
            ColoringRegion(
                id = 109,
                name = "Left Arm",
                element = OutlineElement.Oval(cx = 0.25f, cy = 0.63f, rx = 0.07f, ry = 0.13f),
                priority = 5
            ),
            ColoringRegion(
                id = 110,
                name = "Right Arm",
                element = OutlineElement.Oval(cx = 0.75f, cy = 0.63f, rx = 0.07f, ry = 0.13f),
                priority = 5
            ),
            ColoringRegion(
                id = 111,
                name = "Left Foot",
                element = OutlineElement.Oval(cx = 0.35f, cy = 0.88f, rx = 0.09f, ry = 0.06f),
                priority = 5
            ),
            ColoringRegion(
                id = 112,
                name = "Right Foot",
                element = OutlineElement.Oval(cx = 0.65f, cy = 0.88f, rx = 0.09f, ry = 0.06f),
                priority = 5
            ),
            ColoringRegion(
                id = 113,
                name = "Body",
                element = OutlineElement.Oval(cx = 0.50f, cy = 0.70f, rx = 0.23f, ry = 0.21f),
                priority = 1
            )
        )
    }

    // --- 2. Rocket Ship Regions ---
    val rocketShipRegions: List<ColoringRegion> by lazy {
        listOf(
            ColoringRegion(
                id = 201,
                name = "Inner Porthole Window",
                element = OutlineElement.Circle(cx = 0.50f, cy = 0.40f, radius = 0.055f),
                priority = 10
            ),
            ColoringRegion(
                id = 202,
                name = "Outer Porthole Window",
                element = OutlineElement.Circle(cx = 0.50f, cy = 0.40f, radius = 0.080f),
                priority = 5
            ),
            ColoringRegion(
                id = 203,
                name = "Left Fin",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.38f, 0.50f),
                        PathAction.LineTo(0.22f, 0.67f),
                        PathAction.QuadTo(0.30f, 0.67f, 0.38f, 0.64f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 204,
                name = "Right Fin",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.62f, 0.50f),
                        PathAction.LineTo(0.78f, 0.67f),
                        PathAction.QuadTo(0.70f, 0.67f, 0.62f, 0.64f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 205,
                name = "Fuselage",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.50f, 0.12f),
                        PathAction.CubicTo(0.43f, 0.22f, 0.38f, 0.42f, 0.38f, 0.65f),
                        PathAction.LineTo(0.62f, 0.65f),
                        PathAction.CubicTo(0.62f, 0.42f, 0.57f, 0.22f, 0.50f, 0.12f)
                    ),
                    isClosed = true
                ),
                priority = 1
            ),
            ColoringRegion(
                id = 206,
                name = "Thruster Nozzle",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.44f, 0.65f),
                        PathAction.LineTo(0.41f, 0.72f),
                        PathAction.LineTo(0.59f, 0.72f),
                        PathAction.LineTo(0.56f, 0.65f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 207,
                name = "Rocket Fire",
                element = OutlineElement.Path(
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
                priority = 5
            ),
            ColoringRegion(
                id = 208,
                name = "Top Left Star",
                element = ColoringOutlineTemplates.starElement(cx = 0.20f, cy = 0.22f, size = 0.045f),
                priority = 5
            ),
            ColoringRegion(
                id = 209,
                name = "Top Right Star",
                element = ColoringOutlineTemplates.starElement(cx = 0.80f, cy = 0.20f, size = 0.045f),
                priority = 5
            ),
            ColoringRegion(
                id = 210,
                name = "Bottom Left Star",
                element = ColoringOutlineTemplates.starElement(cx = 0.18f, cy = 0.78f, size = 0.035f),
                priority = 5
            ),
            ColoringRegion(
                id = 211,
                name = "Planet",
                element = OutlineElement.Circle(cx = 0.80f, cy = 0.75f, radius = 0.055f),
                priority = 5
            ),
            ColoringRegion(
                id = 212,
                name = "Planet Ring",
                element = OutlineElement.Oval(cx = 0.80f, cy = 0.75f, rx = 0.090f, ry = 0.025f),
                priority = 2
            )
        )
    }

    // --- 3. Sun and Cloud Regions ---
    val sunAndCloudRegions: List<ColoringRegion> by lazy {
        listOf(
            ColoringRegion(
                id = 301,
                name = "Sun Center",
                element = OutlineElement.Circle(cx = 0.45f, cy = 0.36f, radius = 0.17f),
                priority = 5
            ),
            ColoringRegion(
                id = 302,
                name = "Fluffy Cloud",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.24f, 0.76f),
                        PathAction.CubicTo(0.16f, 0.76f, 0.16f, 0.64f, 0.26f, 0.62f),
                        PathAction.CubicTo(0.28f, 0.52f, 0.42f, 0.50f, 0.48f, 0.57f),
                        PathAction.CubicTo(0.53f, 0.48f, 0.72f, 0.50f, 0.74f, 0.60f),
                        PathAction.CubicTo(0.85f, 0.62f, 0.85f, 0.76f, 0.76f, 0.76f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 303,
                name = "Raindrop Left",
                element = OutlineElement.Oval(cx = 0.32f, cy = 0.84f, rx = 0.012f, ry = 0.022f),
                priority = 5
            ),
            ColoringRegion(
                id = 304,
                name = "Raindrop Middle",
                element = OutlineElement.Oval(cx = 0.50f, cy = 0.84f, rx = 0.012f, ry = 0.022f),
                priority = 5
            ),
            ColoringRegion(
                id = 305,
                name = "Raindrop Right",
                element = OutlineElement.Oval(cx = 0.68f, cy = 0.84f, rx = 0.012f, ry = 0.022f),
                priority = 5
            )
        )
    }

    // --- 4. Cartoon Race Car Regions ---
    val raceCarRegions: List<ColoringRegion> by lazy {
        listOf(
            ColoringRegion(
                id = 401,
                name = "Windshield and Windows",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.41f, 0.46f),
                        PathAction.LineTo(0.56f, 0.46f),
                        PathAction.LineTo(0.63f, 0.55f),
                        PathAction.LineTo(0.41f, 0.55f)
                    ),
                    isClosed = true
                ),
                priority = 10
            ),
            ColoringRegion(
                id = 402,
                name = "Star on Door",
                element = ColoringOutlineTemplates.starElement(cx = 0.50f, cy = 0.61f, size = 0.040f),
                priority = 10
            ),
            ColoringRegion(
                id = 403,
                name = "Rear Wheel Rim",
                element = OutlineElement.Circle(cx = 0.32f, cy = 0.67f, radius = 0.045f),
                priority = 15
            ),
            ColoringRegion(
                id = 404,
                name = "Rear Wheel Tire",
                element = OutlineElement.Circle(cx = 0.32f, cy = 0.67f, radius = 0.085f),
                priority = 10
            ),
            ColoringRegion(
                id = 405,
                name = "Front Wheel Rim",
                element = OutlineElement.Circle(cx = 0.68f, cy = 0.67f, radius = 0.045f),
                priority = 15
            ),
            ColoringRegion(
                id = 406,
                name = "Front Wheel Tire",
                element = OutlineElement.Circle(cx = 0.68f, cy = 0.67f, radius = 0.085f),
                priority = 10
            ),
            ColoringRegion(
                id = 407,
                name = "Headlight",
                element = OutlineElement.Oval(cx = 0.84f, cy = 0.60f, rx = 0.020f, ry = 0.015f),
                priority = 10
            ),
            ColoringRegion(
                id = 408,
                name = "Rear Spoiler",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.12f, 0.46f),
                        PathAction.LineTo(0.24f, 0.46f),
                        PathAction.LineTo(0.23f, 0.50f),
                        PathAction.LineTo(0.12f, 0.50f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 409,
                name = "Car Body",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.14f, 0.65f),
                        PathAction.LineTo(0.14f, 0.58f),
                        PathAction.LineTo(0.28f, 0.58f),
                        PathAction.LineTo(0.38f, 0.44f),
                        PathAction.LineTo(0.58f, 0.44f),
                        PathAction.LineTo(0.68f, 0.56f),
                        PathAction.LineTo(0.86f, 0.58f),
                        PathAction.QuadTo(0.88f, 0.65f, 0.85f, 0.67f),
                        PathAction.LineTo(0.77f, 0.67f),
                        PathAction.QuadTo(0.68f, 0.58f, 0.59f, 0.67f),
                        PathAction.LineTo(0.41f, 0.67f),
                        PathAction.QuadTo(0.32f, 0.58f, 0.23f, 0.67f),
                        PathAction.LineTo(0.14f, 0.67f)
                    ),
                    isClosed = true
                ),
                priority = 1
            )
        )
    }

    // --- 5. Flower & Butterfly Regions ---
    val flowerButterflyRegions: List<ColoringRegion> by lazy {
        listOf(
            ColoringRegion(
                id = 501,
                name = "Flower Center",
                element = OutlineElement.Circle(cx = 0.42f, cy = 0.46f, radius = 0.095f),
                priority = 10
            ),
            ColoringRegion(
                id = 502,
                name = "Petal Top",
                element = OutlineElement.Circle(cx = 0.42f, cy = 0.34f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 503,
                name = "Petal Bottom",
                element = OutlineElement.Circle(cx = 0.42f, cy = 0.58f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 504,
                name = "Petal Top Left",
                element = OutlineElement.Circle(cx = 0.31f, cy = 0.40f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 505,
                name = "Petal Top Right",
                element = OutlineElement.Circle(cx = 0.53f, cy = 0.40f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 506,
                name = "Petal Bottom Left",
                element = OutlineElement.Circle(cx = 0.31f, cy = 0.52f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 507,
                name = "Petal Bottom Right",
                element = OutlineElement.Circle(cx = 0.53f, cy = 0.52f, radius = 0.085f),
                priority = 5
            ),
            ColoringRegion(
                id = 508,
                name = "Left Leaf",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.41f, 0.70f),
                        PathAction.QuadTo(0.28f, 0.67f, 0.25f, 0.75f),
                        PathAction.QuadTo(0.35f, 0.78f, 0.42f, 0.74f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 509,
                name = "Right Leaf",
                element = OutlineElement.Path(
                    actions = listOf(
                        PathAction.MoveTo(0.42f, 0.76f),
                        PathAction.QuadTo(0.54f, 0.72f, 0.58f, 0.80f),
                        PathAction.QuadTo(0.49f, 0.84f, 0.43f, 0.80f)
                    ),
                    isClosed = true
                ),
                priority = 5
            ),
            ColoringRegion(
                id = 510,
                name = "Butterfly Body",
                element = OutlineElement.Oval(cx = 0.74f, cy = 0.28f, rx = 0.016f, ry = 0.050f),
                priority = 10
            ),
            ColoringRegion(
                id = 511,
                name = "Butterfly Wing Top Left",
                element = OutlineElement.Oval(cx = 0.67f, cy = 0.24f, rx = 0.055f, ry = 0.040f),
                priority = 5
            ),
            ColoringRegion(
                id = 512,
                name = "Butterfly Wing Bottom Left",
                element = OutlineElement.Oval(cx = 0.68f, cy = 0.31f, rx = 0.040f, ry = 0.030f),
                priority = 5
            ),
            ColoringRegion(
                id = 513,
                name = "Butterfly Wing Top Right",
                element = OutlineElement.Oval(cx = 0.81f, cy = 0.24f, rx = 0.055f, ry = 0.040f),
                priority = 5
            ),
            ColoringRegion(
                id = 514,
                name = "Butterfly Wing Bottom Right",
                element = OutlineElement.Oval(cx = 0.80f, cy = 0.31f, rx = 0.040f, ry = 0.030f),
                priority = 5
            )
        )
    }
}
