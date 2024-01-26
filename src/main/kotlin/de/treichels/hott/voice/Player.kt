package de.treichels.hott.voice

import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.*
import javax.sound.sampled.AudioFormat.Encoding
import javax.sound.sampled.LineEvent.Type

/**
 * A utility class that can play audio data.
 *
 * @author oliver.treichel@gmx.de
 */
object Player {
    fun play(format: AudioFormat, data: ByteArray, offset: Int = 0, bufferSize: Int = data.size, sync: Boolean = true) {
        play(format, ByteArrayInputStream(data, offset, bufferSize), sync)
    }

    fun play(format: AudioFormat, stream: InputStream, sync: Boolean = true) {
        var sourceDataLine: SourceDataLine? = null

        try {
            sourceDataLine = AudioSystem.getSourceDataLine(format)
            sourceDataLine!!.open()
            sourceDataLine.start()

            val buffer = ByteArray(sourceDataLine.bufferSize)

            while (true) {
                val len = stream.read(buffer)
                if (len == -1) break
                sourceDataLine.write(buffer, 0, len)
            }

            if (sync) sourceDataLine.drain()
        } catch (e: LineUnavailableException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            if (sourceDataLine != null && sourceDataLine.isRunning) sourceDataLine.stop()
            if (sourceDataLine != null && sourceDataLine.isOpen) sourceDataLine.close()
        }
    }

    fun play(stream: AudioInputStream, sync: Boolean = true) {
        val sourceFormat = stream.format
        val rate = sourceFormat.sampleRate
        val channels = sourceFormat.channels
        val targetFormat = AudioFormat(Encoding.PCM_SIGNED, rate, 16, channels, channels * 2, rate, false)

        if (sourceFormat.matches(targetFormat))
            play(sourceFormat, stream, sync)
        else
            play(AudioSystem.getAudioInputStream(targetFormat, stream), sync)
    }

    fun play(clip: Clip, sync: Boolean = true) {
        val latch = CountDownLatch(1)

        clip.addLineListener { e ->
            if (e.type === Type.STOP) {
                if (sync) latch.countDown()
                clip.close()
            }
        }

        clip.start()

        if (sync) latch.await()
    }

    fun play(file: File, sync: Boolean = true) {
        play(AudioSystem.getAudioInputStream(file), sync)
    }
}
