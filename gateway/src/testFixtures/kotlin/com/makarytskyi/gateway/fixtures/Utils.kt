package com.makarytskyi.gateway.fixtures

import kotlin.random.Random

internal object Utils {
    internal fun generateString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charPool[Random.nextInt(charPool.size)] }
            .joinToString("")
    }
}