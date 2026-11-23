package dev.mauicoder.kidsboard.audio

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedurally generates short, playful 16-bit PCM Mono WAV byte arrays completely in-memory.
 * No external audio files or dependencies required.
 */
object AudioSynthesizer {

    const val SAMPLE_RATE = 22050

    /**
     * Creates a canonical 44-byte WAV header for 16-bit mono PCM.
     */
    fun createWavHeader(dataLength: Int, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val totalDataLen = dataLength + 36
        val byteRate = sampleRate * 2 // 1 channel * 16 bits / 8
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF chunk
        header.put('R'.code.toByte())
        header.put('I'.code.toByte())
        header.put('F'.code.toByte())
        header.put('F'.code.toByte())
        header.putInt(totalDataLen)
        header.put('W'.code.toByte())
        header.put('A'.code.toByte())
        header.put('V'.code.toByte())
        header.put('E'.code.toByte())

        // fmt chunk
        header.put('f'.code.toByte())
        header.put('m'.code.toByte())
        header.put('t'.code.toByte())
        header.put(' '.code.toByte())
        header.putInt(16) // Subchunk1Size (16 for PCM)
        header.putShort(1) // AudioFormat (1 = PCM)
        header.putShort(1) // NumChannels (1 = Mono)
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(2) // BlockAlign (1 * 16 / 8)
        header.putShort(16) // BitsPerSample

        // data chunk
        header.put('d'.code.toByte())
        header.put('a'.code.toByte())
        header.put('t'.code.toByte())
        header.put('a'.code.toByte())
        header.putInt(dataLength)

        return header.array()
    }

    /**
     * Converts an array of 16-bit PCM Short samples into a complete, valid WAV byte array.
     */
    fun samplesToWav(samples: ShortArray, sampleRate: Int = SAMPLE_RATE): ByteArray {
        val byteBuffer = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in samples) {
            byteBuffer.putShort(sample)
        }
        val pcmData = byteBuffer.array()
        val header = createWavHeader(pcmData.size, sampleRate)

        val out = ByteArrayOutputStream(header.size + pcmData.size)
        out.write(header)
        out.write(pcmData)
        return out.toByteArray()
    }

    /**
     * Generates a bubbly downward pitch pop (e.g. for sticker placement).
     */
    fun createPopWav(
        durationMs: Int = 110,
        startFreq: Float = 700f,
        endFreq: Float = 220f,
        sampleRate: Int = SAMPLE_RATE
    ): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val samples = ShortArray(totalSamples)
        var phase = 0.0
        val freqRatio = (endFreq / startFreq).toDouble()

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / totalSamples // 0.0 to 1.0
            // Fast exponential frequency descent
            val currentFreq = startFreq.toDouble() * freqRatio.pow(t)
            phase += 2.0 * PI * currentFreq / sampleRate

            // Pop envelope: quick attack (5ms), smooth exponential decay
            val envelope = if (t < 0.05) {
                t / 0.05
            } else {
                exp(-4.5 * (t - 0.05))
            }

            val value = sin(phase) * envelope
            samples[i] = (value * 28000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samplesToWav(samples, sampleRate)
    }

    /**
     * Generates a sweet harmonic chime (e.g. for magical sticker placement).
     */
    fun createChimeWav(
        durationMs: Int = 260,
        fundamentalFreq: Float = 880f,
        sampleRate: Int = SAMPLE_RATE
    ): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val samples = ShortArray(totalSamples)
        var phase1 = 0.0
        var phase2 = 0.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / totalSamples
            phase1 += 2.0 * PI * fundamentalFreq.toDouble() / sampleRate
            phase2 += 2.0 * PI * (fundamentalFreq * 2.01) / sampleRate

            // Smooth exponential decay
            val envelope = exp(-5.0 * t)
            val wave = (sin(phase1) * 0.75 + sin(phase2) * 0.25) * envelope
            samples[i] = (wave * 26000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samplesToWav(samples, sampleRate)
    }

    /**
     * Generates a cartoon whoosh sound (e.g. for canvas clearing).
     */
    fun createWhooshWav(
        durationMs: Int = 360,
        sampleRate: Int = SAMPLE_RATE
    ): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val samples = ShortArray(totalSamples)
        val random = Random(42)

        // Resonant filter state
        var filterOut = 0.0
        var filterSpeed = 0.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / totalSamples
            // Whoosh envelope: parabolic arc rising and falling
            val envelope = sin(t * PI).let { it * it }

            // Sweeping center frequency for the whoosh resonance
            val centerFreq = 400.0 + 1200.0 * sin(t * PI)
            val whiteNoise = (random.nextFloat() * 2f - 1f).toDouble()

            // Simple 2-pole resonant filter
            val q = 0.35
            val w = 2.0 * PI * centerFreq / sampleRate
            filterSpeed += (whiteNoise - filterOut) * w
            filterOut += filterSpeed * w * q

            val sampleVal = filterOut * envelope
            samples[i] = (sampleVal * 30000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samplesToWav(samples, sampleRate)
    }

    /**
     * Generates a gentle textured scribble/crayon friction sound that loops seamlessly.
     */
    fun createScribbleWav(
        durationMs: Int = 450,
        sampleRate: Int = SAMPLE_RATE
    ): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val samples = ShortArray(totalSamples)
        val random = Random(1234)

        var lowPass = 0.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / totalSamples
            // Gentle rhythmic friction modulation (simulating back-and-forth strokes)
            val modulation = 0.6 + 0.4 * sin(2.0 * PI * 8.0 * t)

            // Low-pass filtered noise to remove harsh hiss, giving a warm crayon/pencil paper friction
            val noise = (random.nextFloat() * 2f - 1f).toDouble()
            lowPass += (noise - lowPass) * 0.18

            // Windowing at boundaries to ensure seamless click-free looping
            val window = 0.5 * (1.0 - cos(2.0 * PI * t))

            val sampleVal = lowPass * modulation * window
            samples[i] = (sampleVal * 20000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samplesToWav(samples, sampleRate)
    }

    /**
     * Generates a clean, subtle UI click sound for button interactions.
     */
    fun createClickWav(
        durationMs: Int = 28,
        sampleRate: Int = SAMPLE_RATE
    ): ByteArray {
        val totalSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val samples = ShortArray(totalSamples)
        var phase = 0.0

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / totalSamples
            phase += 2.0 * PI * 1800.0 / sampleRate
            val envelope = exp(-18.0 * t)
            val sampleVal = sin(phase) * envelope
            samples[i] = (sampleVal * 22000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samplesToWav(samples, sampleRate)
    }
}
