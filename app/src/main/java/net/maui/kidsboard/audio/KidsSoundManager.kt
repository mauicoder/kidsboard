package net.maui.kidsboard.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Manages playback of procedural sound effects with SoundPool.
 * Handles pitch scaling, drawing loops, and mute status.
 */
class KidsSoundManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var soundPool: SoundPool? = null

    private var popSoundId: Int = 0
    private var chimeSoundId: Int = 0
    private var whooshSoundId: Int = 0
    private var scribbleSoundId: Int = 0
    private var clickSoundId: Int = 0

    private var drawingStreamId: Int = 0
    private var isDrawingActive: Boolean = false

    var isSoundEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) {
                stopDrawingLoop()
            }
        }

    // Pentatonic scale pitch steps for playful musical stamping
    private val stampPitchScales = floatArrayOf(1.0f, 1.122f, 1.260f, 1.335f, 1.498f, 1.682f, 1.888f, 2.0f)
    private var stampPitchIndex = 0
    private var lastStampTimeMs = 0L

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val pool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(attributes)
            .build()

        soundPool = pool

        scope.launch {
            try {
                val audioDir = File(context.cacheDir, "kids_audio").apply { mkdirs() }

                val popFile = getOrCreateSoundFile(audioDir, "pop_v1.wav") { AudioSynthesizer.createPopWav() }
                val chimeFile = getOrCreateSoundFile(audioDir, "chime_v1.wav") { AudioSynthesizer.createChimeWav() }
                val whooshFile = getOrCreateSoundFile(audioDir, "whoosh_v1.wav") { AudioSynthesizer.createWhooshWav() }
                val scribbleFile = getOrCreateSoundFile(audioDir, "scribble_v1.wav") { AudioSynthesizer.createScribbleWav() }
                val clickFile = getOrCreateSoundFile(audioDir, "click_v1.wav") { AudioSynthesizer.createClickWav() }

                popSoundId = pool.load(popFile.absolutePath, 1)
                chimeSoundId = pool.load(chimeFile.absolutePath, 1)
                whooshSoundId = pool.load(whooshFile.absolutePath, 1)
                scribbleSoundId = pool.load(scribbleFile.absolutePath, 1)
                clickSoundId = pool.load(clickFile.absolutePath, 1)
            } catch (e: Exception) {
                // Non-critical: app continues seamlessly if audio setup encounters an error
                e.printStackTrace()
            }
        }
    }

    private inline fun getOrCreateSoundFile(dir: File, fileName: String, generator: () -> ByteArray): File {
        val file = File(dir, fileName)
        if (!file.exists() || file.length() == 0L) {
            val bytes = generator()
            FileOutputStream(file).use { it.write(bytes) }
        }
        return file
    }

    /**
     * Plays a bubble pop or chime with escalating pitch if multiple stamps are placed in sequence.
     */
    fun playStamp(isMagical: Boolean = false) {
        if (!isSoundEnabled) return
        val pool = soundPool ?: return

        val soundId = if (isMagical && chimeSoundId != 0) chimeSoundId else popSoundId
        if (soundId == 0) return

        val now = System.currentTimeMillis()
        if (now - lastStampTimeMs > 2200) {
            stampPitchIndex = 0
        } else {
            stampPitchIndex = (stampPitchIndex + 1) % stampPitchScales.size
        }
        lastStampTimeMs = now

        val pitch = stampPitchScales[stampPitchIndex]
        pool.play(soundId, 0.9f, 0.9f, 1, 0, pitch)
    }

    /**
     * Starts continuous gentle crayon/marker friction sound while drawing.
     */
    fun startDrawingLoop() {
        if (!isSoundEnabled || isDrawingActive) return
        val pool = soundPool ?: return
        if (scribbleSoundId == 0) return

        isDrawingActive = true
        // Loop continuously (-1)
        drawingStreamId = pool.play(scribbleSoundId, 0.45f, 0.45f, 1, -1, 1.0f)
    }

    /**
     * Stops continuous drawing friction sound when all fingers lift.
     */
    fun stopDrawingLoop() {
        isDrawingActive = false
        if (drawingStreamId != 0) {
            soundPool?.stop(drawingStreamId)
            drawingStreamId = 0
        }
    }

    /**
     * Plays cartoon sweep/whoosh when canvas is cleared.
     */
    fun playClearWhoosh() {
        if (!isSoundEnabled) return
        val pool = soundPool ?: return
        if (whooshSoundId != 0) {
            pool.play(whooshSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    /**
     * Plays subtle UI click for toolbar and dialog buttons.
     */
    fun playClick() {
        if (!isSoundEnabled) return
        val pool = soundPool ?: return
        if (clickSoundId != 0) {
            pool.play(clickSoundId, 0.4f, 0.4f, 1, 0, 1.0f)
        }
    }

    /**
     * Releases audio resources when activity/session ends.
     */
    fun release() {
        stopDrawingLoop()
        soundPool?.release()
        soundPool = null
    }
}
