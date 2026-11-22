package net.maui.kidsboard.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioSynthesizerTest {

    private fun verifyWavStructure(bytes: ByteArray, expectedSampleRate: Int = AudioSynthesizer.SAMPLE_RATE) {
        assertTrue(bytes.size >= 44)
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        // "RIFF"
        val riff = String(bytes, 0, 4)
        assertEquals("RIFF", riff)

        val totalDataLen = buffer.getInt(4)
        assertEquals(bytes.size - 8, totalDataLen)

        // "WAVE"
        val wave = String(bytes, 8, 4)
        assertEquals("WAVE", wave)

        // "fmt "
        val fmt = String(bytes, 12, 4)
        assertEquals("fmt ", fmt)

        val subchunk1Size = buffer.getInt(16)
        assertEquals(16, subchunk1Size)

        val audioFormat = buffer.getShort(20).toInt()
        assertEquals(1, audioFormat) // PCM

        val numChannels = buffer.getShort(22).toInt()
        assertEquals(1, numChannels) // Mono

        val sampleRate = buffer.getInt(24)
        assertEquals(expectedSampleRate, sampleRate)

        val byteRate = buffer.getInt(28)
        assertEquals(expectedSampleRate * 2, byteRate)

        val blockAlign = buffer.getShort(32).toInt()
        assertEquals(2, blockAlign)

        val bitsPerSample = buffer.getShort(34).toInt()
        assertEquals(16, bitsPerSample)

        // "data"
        val dataTag = String(bytes, 36, 4)
        assertEquals("data", dataTag)

        val dataSize = buffer.getInt(40)
        assertEquals(bytes.size - 44, dataSize)
        assertTrue(dataSize > 0)
    }

    @Test
    fun testPopWavGeneration() {
        val wav = AudioSynthesizer.createPopWav()
        verifyWavStructure(wav)
    }

    @Test
    fun testChimeWavGeneration() {
        val wav = AudioSynthesizer.createChimeWav()
        verifyWavStructure(wav)
    }

    @Test
    fun testWhooshWavGeneration() {
        val wav = AudioSynthesizer.createWhooshWav()
        verifyWavStructure(wav)
    }

    @Test
    fun testScribbleWavGeneration() {
        val wav = AudioSynthesizer.createScribbleWav()
        verifyWavStructure(wav)
    }

    @Test
    fun testClickWavGeneration() {
        val wav = AudioSynthesizer.createClickWav()
        verifyWavStructure(wav)
    }
}
