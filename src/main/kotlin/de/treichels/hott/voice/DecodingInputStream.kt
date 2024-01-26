package de.treichels.hott.voice

import java.io.InputStream

internal class DecodingInputStream(private val inputStream: InputStream) : InputStream() {
    private val codec = ADPCMCodec()
    private val buffer = IntArray(4)
    private var index = 4


    override fun available(): Int {
        // decoding enlarges the data size by factor of 4
        return inputStream.available() * 4
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
        if (index > 3) {
            // buffer underflow - fetch next value from stream
            val b = inputStream.read()

            if (b == -1) return -1 // end of stream

            index = 0 // reset buffer

            val lowNibble = (b and 0x0F).toByte()
            val highNibble = (b and 0xF0 shr 4).toByte()
            var pcm: Int

            // decode low nibble
            pcm = codec.decode(lowNibble) * 16 // convert 12-bit to 16-bit
            buffer[0] = pcm and 0x00ff // low byte
            buffer[1] = pcm and 0xff00 shr 8 // high byte

            // decode high nibble
            pcm = codec.decode(highNibble) * 16 // convert 12-bit to 16-bit
            buffer[2] = pcm and 0x00ff // low byte
            buffer[3] = pcm and 0xff00 shr 8 // high byte
        }

        return buffer[index++]
    }

    @Synchronized

    override fun reset() {
        inputStream.reset()
    }


    override fun skip(n: Long): Long {
        return inputStream.skip(n)
    }
}