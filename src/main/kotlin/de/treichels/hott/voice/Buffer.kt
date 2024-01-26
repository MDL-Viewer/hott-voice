package de.treichels.hott.voice

/**
 * A rolling buffer with a fixed capacity.
 *
 * As new elements are added to the buffer, previous elements will be removed if the capacity is reached.
 *
 * @author Oliver Treichel &lt;oli@treichels.de&gt;
 */
class Buffer(capacity: Int) {
    /** all elements in this buffer  */
    private val elements: IntArray = IntArray(capacity)

    /** the pointer to the next index where new values are inserted  */
    private var pointer = 0

    /**
     * Get the current capacity of the Buffer.
     *
     * @return The capacity
     */
    val size: Int
        get() = elements.size

    /**
     * Add a new element to the buffer. If the capacity is reached, older values will be automatically removed from the buffer.
     *
     * @param nextElement The new element.
     */
    fun add(nextElement: Int) {
        elements[pointer] = nextElement
        pointer++
        if (pointer == elements.size) pointer = 0
    }

    /**
     * Get the element at the specified position. Where `get(0)` will return the oldest (first added) element and `get(capacity -1)` will
     * return the newest (least added) element.
     *
     * @param index The position were `0 <= index < capacity`.
     * @return The element at the specified position.
     * @throws IndexOutOfBoundsException if index is negative or greater or equal to the capacity.
     */
    operator fun get(index: Int): Int {
        var i = index + pointer
        if (i >= elements.size) i -= elements.size
        return elements[i]
    }

    override fun toString(): String {
        val b = StringBuilder()

        b.append("Buffer[")

        for (i in elements.indices) {
            if (i > 0) b.append(", ")
            b.append(get(i))
        }

        b.append("]")

        return b.toString()
    }
}
