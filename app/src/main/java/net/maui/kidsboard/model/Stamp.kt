package net.maui.kidsboard.model

/**
 * Categorized collection of kid-friendly emoji stamps.
 */
data class StampCategory(
    val title: String,
    val stamps: List<String>
)

object StampRepository {
    val categories: List<StampCategory> = listOf(
        StampCategory(
            title = "Favorites ⭐",
            stamps = listOf("⭐", "❤️", "🌈", "✨", "🎈", "👑")
        ),
        StampCategory(
            title = "Animals 🐾",
            stamps = listOf("🐶", "🐱", "🦁", "🦖", "🦄", "🐼", "🐵", "🐸")
        ),
        StampCategory(
            title = "Fun & Treats 🍦",
            stamps = listOf("🌸", "☀️", "🌙", "🍀", "🦋", "🍦", "🍕", "🍎")
        ),
        StampCategory(
            title = "Vehicles & Art 🚀",
            stamps = listOf("🚀", "🚗", "✈️", "🚂", "⛵", "🎨")
        )
    )

    val allStamps: List<String> = categories.flatMap { it.stamps }
    const val DEFAULT_STAMP: String = "⭐"
}
