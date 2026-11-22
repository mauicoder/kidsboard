package net.maui.kidsboard.model

/**
 * Categorized collection of kid-friendly emoji stamps.
 */
data class StampCategory(
    val title: String,
    val stamps: List<String>
)

object StampRepository {
    const val FULL_RAINBOW: String = "FULL_RAINBOW"

    val categories: List<StampCategory> = listOf(
        StampCategory(
            title = "Favorites ⭐",
            stamps = listOf("⭐", "❤️", FULL_RAINBOW, "🌈", "✨", "🎈", "👑")
        ),
        StampCategory(
            title = "Animals 🐾",
            stamps = listOf("🐶", "🐱", "🦁", "🦖", "🦄", "🐼", "🐵", "🐸")
        ),
        StampCategory(
            title = "Fun & Treats 🍦",
            stamps = listOf(FULL_RAINBOW, "🌸", "☀️", "🌙", "🍀", "🦋", "🍦", "🍕", "🍎")
        ),
        StampCategory(
            title = "Vehicles & Art 🚀",
            stamps = listOf("🚀", "🚗", "✈️", "🚂", "⛵", "🎨")
        )
    )

    val allStamps: List<String> = categories.flatMap { it.stamps }
    const val DEFAULT_STAMP: String = "⭐"
}
