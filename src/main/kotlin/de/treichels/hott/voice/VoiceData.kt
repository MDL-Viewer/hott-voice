package de.treichels.hott.voice

import org.apache.commons.io.IOUtils
import java.io.*
import java.nio.file.Files
import java.util.*
import javax.sound.sampled.*
import javax.sound.sampled.AudioFormat.Encoding

/**
 * Representation of a single announcement in a voice data file (.vdf).
 *
 * @author oliver.treichel@gmx.de
 */
class VoiceData(name: String, val rawData: ByteArray) : Serializable, ObservableBase() {
    companion object {
        /** Default audio format 11 kHz 16-bit signed PCM mono  */
        private val audioFormat = AudioFormat(Encoding.PCM_SIGNED, 11025f, 16, 1, 2, 11025f, false)

        fun forStream(sourceAudioStream: AudioInputStream, name: String, volume: Double = 1.0): VoiceData {
            // read from stream
            val sourceFormat = sourceAudioStream.format
            val rate = sourceFormat.sampleRate
            val channels = sourceFormat.channels

            // convert to from MP3 or OGG to PCM
            val pcmFormat = AudioFormat(Encoding.PCM_SIGNED, rate, 16, channels, channels * 2, rate, false)
            val pcmAudioStream = if (sourceFormat.encoding === Encoding.PCM_SIGNED)
                sourceAudioStream
            else
                AudioSystem.getAudioInputStream(pcmFormat, sourceAudioStream)

            // convert sample rate and channels
            val targetAudioStream = if (pcmFormat.matches(audioFormat))
                pcmAudioStream
            else
                AudioSystem.getAudioInputStream(audioFormat, pcmAudioStream)

            // encode to ADPCM
            val encodedStream = ADPCMCodec.encode(targetAudioStream, volume)
            return VoiceData(name, IOUtils.toByteArray(encodedStream))
        }

        fun forFile(soundFile: File, volume: Double = 1.0): VoiceData {
            // read from file
            val sourceAudioStream = AudioSystem.getAudioInputStream(soundFile)
            val fileName = soundFile.name
            val dot = fileName.lastIndexOf(".")
            val name = fileName.substring(0, dot)
            return forStream(sourceAudioStream, name, volume)
        }
    }
    
    var name = name
        set(name) {
            field = if (name.length > 17) name.substring(0, 18) else name
            invalidate()
        }

    val audioInputStream: AudioInputStream
        get() = AudioInputStream(pcmInputStream, audioFormat, 2L * rawData.size.toLong())

    val pcmData: ByteArray
        get() = ADPCMCodec.decode(rawData)

    val pcmInputStream: InputStream
        get() = ADPCMCodec.decode(rawInputStream)

    val rawInputStream: InputStream
        get() = ByteArrayInputStream(rawData)

    fun play() {
        try {
            Player.play(audioFormat, pcmInputStream)
        } catch (e: LineUnavailableException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun toString(): String {
        return String.format("VoiceData [name=%s]", this.name)
    }


    fun writeVox(voxFile: File) {
        Files.write(voxFile.toPath(), rawData)
    }


    fun writeWav(wavFile: File) {
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VoiceData

        if (!Arrays.equals(rawData, other.rawData)) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(rawData)
        result = 31 * result + (name.hashCode())
        return result
    }

    fun clone(): VoiceData = VoiceData(this.name, this.rawData)
}
