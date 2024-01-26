package de.treichels.hott.voice

import java.io.IOException
import java.io.InputStream

internal class EncodingInputStream(private val inputStream: InputStream, private val volume: Double=1.0) : InputStream() {
    private val codec = ADPCMCodec()
    private val buffer = ByteArray(2)


    override fun available(): Int {
        // encoding reduces the data size by factor of 4
        return (inputStream.available() + 3) / 4
    }


    override fun close() {
        inputStream.close()
    }

    @Synchronized override fun mark(readlimit: Int) {
        inputStream.mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return inputStream.markSupported()
    }


    override fun read(): Int {
        // read 16-bit for low nibble (unsigned)
        var pcm = readShort()
        var adpcm = -1

        if (pcm != -1) {
            // convert to signed 16-bit and reduce to 12-bit
            var signed = (pcm.toShort() / 16 * volume).toInt().toShort()

            // convert to low nibble
            adpcm = codec.encode(signed).toInt()

            // read 16 bit for high nibble (unsigned)
            pcm = readShort()
            if (pcm != -1) {
                // convert to signed 16-bit and reduce to 12-bit
                signed = (pcm.toShort() / 16 * volume).toInt().toShort()

                // convert to high nibble
                adpcm = adpcm or (codec.encode(signed).toInt() shl 4)
            }
        }

        return adpcm
    }

    /**
     * Read unsigned 16-bit value from stream.
     *
     * @return
     * @throws IOException
     */

    private fun readShort(): Int {
        val len = inputStream.read(buffer)
        return if (len == -1) -1 else buffer[0].toInt() and 0xFF or (buffer[1].toInt() and 0xFF shl 8)
    }

    @Synchronized

    override fun reset() {
        inputStream.reset()
    }


    override fun skip(n: Long): Long {
        return inputStream.skip(n)
    }
}
