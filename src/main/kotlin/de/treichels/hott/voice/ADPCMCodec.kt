package de.treichels.hott.voice

import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Codec to encode / decode ADPCM (VOX) audio data.
 *
 * see [http://faculty.salina.k-state.edu/tim/vox/dialogic_adpcm.pdf](http://faculty.salina.k-state.edu/tim/vox/dialogic_adpcm.pdf) for details.
 *
 * @author oliver.treichel@gmx.de
 */
class ADPCMCodec {
    /** index into step size table  */
    private var index = 0

    /** last PCM value  */
    private var last = 0

    /**
     * Decode an ADPCM encoded 4-bit sample to 16-bit signed PCM.
     *
     * @param adpcm ADPCM encoded sample (4 bit)
     * @return The decoded PCM value (12 bit signed)
     */
    fun decode(adpcm: Byte): Short {
        // convert byte into BitSet
        val bits = BitSet.valueOf(byteArrayOf(adpcm))

        // current step size
        val stepSize = STEP_SIZES[index]

        // calculate difference
        var diff = stepSize shr 3
        // magnitude bit 3
        if (bits[2]) diff += stepSize
        // magnitude bit 2
        if (bits[1]) diff += stepSize shr 1
        // magnitude bit 1
        if (bits[0]) diff += stepSize shr 2
        // sign
        if (bits[3]) diff = -diff

        // new PCM value
        last += diff

        // clipping
        if (last > MAX_PCM) last = MAX_PCM
        if (last < MIN_PCM) last = MIN_PCM

        // new step size
        index += QUANTIZER[adpcm.toInt() and 7]

        // clipping
        if (index < MIN_INDEX) index = MIN_INDEX
        if (index > MAX_INDEX) index = MAX_INDEX

        return last.toShort()
    }

    /**
     * Encode a 16-bit signed PCM value to 4-bit ADPCM.
     *
     * @param pcm
     * The PCM value (12 bit signed)
     * @return The ADPCM encoded sample (4 bit)
     */
    fun encode(pcm: Short): Byte {
        var stepSize = STEP_SIZES[index]
        var diff = pcm - last
        val bits = BitSet(4)

        // sign
        if (diff < 0) {
            bits[3] = true
            diff = -diff
        }

        // magnitude bit 3
        if (diff >= stepSize) {
            bits[2] = true
            diff -= stepSize
        }

        // magnitude bit 2
        stepSize = stepSize shr 1
        if (diff >= stepSize) {
            bits[1] = true
            diff -= stepSize
        }

        // magnitude bit 1
        stepSize = stepSize shr 1
        if (diff >= stepSize) bits[0] = true


        val adpcm = if (bits.isEmpty) 0 else  bits.toByteArray()[0]
        last = decode(adpcm).toInt()

        return adpcm
    }

    companion object {
        private val QUANTIZER = intArrayOf(-1, -1, -1, -1, 2, 4, 6, 8)
        private val STEP_SIZES = intArrayOf(7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767)
        private const val MIN_PCM = -2048 // 12-bit
        private const val MAX_PCM = 2047 // 12-bit
        private const val MIN_INDEX = 0
        private val MAX_INDEX = STEP_SIZES.size - 1

        /**
         * Decode 4-bit ADPCM encoded data to 16-bit signed PCM (compression ratio 1:4).
         *
         * @param data
         * ADPCM encoded data. Each byte contains two 4-bit samples.
         * @return The PCM decoded data.
         * @throws IOException
         */

        fun decode(data: ByteArray): ByteArray {
            return IOUtils.toByteArray(decode(ByteArrayInputStream(data)))
        }

        /**
         * Decode 4-bit ADPCM encoded data to 16-bit signed PCM (compression ratio 1:4).
         *
         * @param source
         * A stream of ADPCM encoded data. Each byte contains two 4-bit samples.
         * @return A stream of PCM data.
         * @throws IOException
         */

        fun decode(source: InputStream): InputStream {
            return DecodingInputStream(source)
        }

        /**
         * Encode 16-bit signed PCM data to 4-bit ADPCM encoded data (compression ratio 4:1).
         *
         * @param data
         * The PCM data.
         *
         * @return The ADPCM encoded data. Each byte contains two 4-bit samples.
         * @throws IOException
         */

        fun encode(data: ByteArray): ByteArray {
            return IOUtils.toByteArray(encode(ByteArrayInputStream(data)))
        }

        /**
         * Encode 16-bit signed PCM data to 4-bit ADPCM encoded data (compression ratio 4:1).
         *
         * @param source
         * A stream of PCM data.
         * @return A stream of ADPCM encoded data. Each byte contains two 4-bit samples.
         * @throws IOException
         */
        @JvmOverloads

        fun encode(source: InputStream, volume: Double = 1.0): InputStream {
            return EncodingInputStream(source, volume)
        }
    }
}
