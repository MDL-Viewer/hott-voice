package de.treichels.hott.voice

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrimInputStreamTest {
    private val expected = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

    @Test
    fun testReadChunk() {
        val data = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val stream = TrimInputStream(data.inputStream(), 4)

        assertEquals(4, stream.readChunk())
        assertEquals(1, stream.read())
        assertEquals(2, stream.read())
        assertEquals(3, stream.read())
        assertEquals(4, stream.read())

        assertEquals(4, stream.readChunk())
        assertEquals(5, stream.read())
        assertEquals(6, stream.read())
        assertEquals(7, stream.read())
        assertEquals(8, stream.read())

        assertEquals(-1, stream.readChunk())
    }

    @Test
    fun testIsNull() {
        val data = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0)

        val stream = TrimInputStream(data.inputStream(), 4)

        assertEquals(4, stream.readChunk())
        assertTrue(stream.isNull())

        assertEquals(4, stream.readChunk())
        assertFalse(stream.isNull())

        assertEquals(4, stream.readChunk())
        assertFalse(stream.isNull())

        assertEquals(-1, stream.readChunk())
    }

    @Test
    fun testNormal() {
        val data = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val target = TrimInputStream(data.inputStream(), 4)
        val result = ByteArray(data.size)
        val num = target.read(result)

        assertEquals(expected.size, num)
        assertArrayEquals(expected, result.copyOfRange(0, num))
    }

    @Test
    fun skipStart() {
        val data = byteArrayOf(0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8)
        val target = TrimInputStream(data.inputStream(), 4)
        val result = ByteArray(data.size)
        val num = target.read(result)

        assertEquals(expected.size, num)
        assertArrayEquals(expected, result.copyOfRange(0, num))
    }

    @Test
    fun skipEnd() {
        val data = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0)
        val target = TrimInputStream(data.inputStream(), 4)
        val result = ByteArray(data.size)
        val num = target.read(result)

        assertEquals(expected.size, num)
        assertArrayEquals(expected, result.copyOfRange(0, num))
    }

    @Test
    fun testMid() {
        val data = byteArrayOf(1, 2, 0, 0, 0, 3, 4, 5, 0, 0, 0, 0, 6, 7, 8, 9)
        val target = TrimInputStream(data.inputStream(), 4)
        val result = ByteArray(data.size)
        val num = target.read(result)

        assertEquals(data.size, num)
        assertArrayEquals(data, result)
    }

    @Test
    fun testMixed() {
        val data = byteArrayOf(0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 3, 4, 5, 0, 0, 0, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val expected = byteArrayOf(0, 0, 1, 2, 0, 0, 0, 0, 3, 4, 5, 0, 0, 0, 6, 7, 8, 9, 0, 0)
        val target = TrimInputStream(data.inputStream(), 4)
        val result = ByteArray(data.size)
        val num = target.read(result)

        assertEquals(expected.size, num)
        assertArrayEquals(expected, result.copyOfRange(0, num))
    }
}