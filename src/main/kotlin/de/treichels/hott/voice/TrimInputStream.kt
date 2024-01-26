package de.treichels.hott.voice

import java.io.InputStream
import java.util.*

class TrimInputStream(private val source: InputStream, private val chunkSize: Int) : InputStream() {
    private var startDetected = false
    private val buffer = LinkedList<Int>()

    internal fun readChunk(): Int {
        val data = ByteArray(chunkSize)
        val len = source.read(data)

        return if (len < chunkSize)
            -1
        else {
            (0 until chunkSize).mapTo(buffer) { data[it].toInt() and 0xff }
            chunkSize
        }
    }

    internal fun isNull() = buffer.filterNot { it == 0 }.isEmpty()

    override fun read(): Int {
        if (buffer.size > 0) {
            // use buffer first
            return buffer.remove()
        } else {
            // read next chunk from source
            if (readChunk() == -1) {
                return -1
            }
        }

        // skip null bytes at start
        if (!startDetected) {
            while (isNull()) {
                // discard bytes
                buffer.clear()

                // read next chunk or enf of stream
                if (readChunk() == -1) {
                    return -1
                }
            }
            startDetected = true
        }

        // skip null bytes at end
        if (isNull()) {
            do {
                if (readChunk() == -1) {
                    // end of stream detected - discard null bytes
                    buffer.clear()
                    return -1
                }
            } while (isNull())
        }

        return read()
    }
}