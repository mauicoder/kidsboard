package net.maui.kidsboard.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists user sound settings across app launches.
 */
class SoundPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    companion object {
        private const val PREFS_NAME = "kidsboard_preferences"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
    }
}
