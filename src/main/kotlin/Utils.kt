package murphytalk.utils

fun <K, V> Map<K, V>.mergeReduce(other: Map<K, V>, reduce: (V, V) -> V = { _, b -> b }): Map<K, V> =
        this.toMutableMap().apply { other.forEach { merge(it.key, it.value, reduce) } }