package dev.mauicoder.kidsboard.model

import androidx.compose.ui.graphics.Color

enum class BackgroundCategory {
    PAPER,
    COLORING
}

sealed class CanvasBackground(
    val id: String,
    val title: String,
    val category: BackgroundCategory,
    val baseColor: Color
) {
    val isDark: Boolean get() = this is Chalkboard

    // --- Paper Styles ---
    data object PlainWhite : CanvasBackground(
        id = "white",
        title = "Blank",
        category = BackgroundCategory.PAPER,
        baseColor = Color.White
    )

    data object Chalkboard : CanvasBackground(
        id = "chalkboard",
        title = "Chalkboard",
        category = BackgroundCategory.PAPER,
        baseColor = Color(0xFF1E2621)
    )

    data object GridPaper : CanvasBackground(
        id = "grid",
        title = "Grid",
        category = BackgroundCategory.PAPER,
        baseColor = Color(0xFFFBFBFB)
    )

    data object LinedPaper : CanvasBackground(
        id = "lined",
        title = "Lined",
        category = BackgroundCategory.PAPER,
        baseColor = Color(0xFFFBFBFB)
    )

    // --- Coloring Book Templates ---
    data object CuteBear : CanvasBackground(
        id = "cute_bear",
        title = "Teddy Bear",
        category = BackgroundCategory.COLORING,
        baseColor = Color.White
    )

    data object RocketShip : CanvasBackground(
        id = "rocket_ship",
        title = "Rocket",
        category = BackgroundCategory.COLORING,
        baseColor = Color.White
    )

    data object SunAndCloud : CanvasBackground(
        id = "sun_cloud",
        title = "Sun & Cloud",
        category = BackgroundCategory.COLORING,
        baseColor = Color.White
    )

    data object RaceCar : CanvasBackground(
        id = "race_car",
        title = "Race Car",
        category = BackgroundCategory.COLORING,
        baseColor = Color.White
    )

    data object FlowerButterfly : CanvasBackground(
        id = "flower_butterfly",
        title = "Flower",
        category = BackgroundCategory.COLORING,
        baseColor = Color.White
    )

    companion object {
        val PAPER_STYLES: List<CanvasBackground> by lazy {
            listOf(PlainWhite, Chalkboard, GridPaper, LinedPaper)
        }
        val COLORING_TEMPLATES: List<CanvasBackground> by lazy {
            listOf(CuteBear, RocketShip, SunAndCloud, RaceCar, FlowerButterfly)
        }
        val ALL: List<CanvasBackground> by lazy {
            PAPER_STYLES + COLORING_TEMPLATES
        }

        fun fromId(id: String): CanvasBackground {
            return ALL.firstOrNull { it.id == id } ?: PlainWhite
        }
    }
}
