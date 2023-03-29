fun <K, V> Map<K, V>.mergeReduce(other: Map<K, V>, reduce: (V, V) -> V = { _, b -> b }): Map<K, V> =
        this.toMutableMap().apply { other.forEach { merge(it.key, it.value, reduce) } }
        
class FixedSizePriorityQueue<T : Comparable<T>>(private val maxSize: Int) {
    private val queue = PriorityQueue<T>()

    fun add(element: T) {
        if (queue.size < maxSize) {
            queue.offer(element)
        } else if (queue.peek() < element) {
            queue.poll()
            queue.offer(element)
        }
    }

    fun getLargest(): List<T> {
        return queue.toList()
    }

    fun getSmallest(): List<T> {
        return queue.reversed().toList()
    }
}
