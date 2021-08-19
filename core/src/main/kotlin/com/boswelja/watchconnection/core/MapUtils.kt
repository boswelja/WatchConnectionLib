package com.boswelja.watchconnection.core

fun <K, V> Map<K, V>.firstOrNull(predicate: (Map.Entry<K, V>) -> Boolean): V? {
    forEach { entry ->
        if (predicate(entry)) return entry.value
    }
    return null
}
