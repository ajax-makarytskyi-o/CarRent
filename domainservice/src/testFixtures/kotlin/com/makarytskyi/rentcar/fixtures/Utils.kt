package com.makarytskyi.rentcar.fixtures

import java.time.Clock
import java.time.Duration
import java.util.Date
import kotlin.random.Random

object Utils {
    internal fun generateString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charPool[Random.nextInt(charPool.size)] }
            .joinToString("")
    }

    internal fun getDateFromNow(days: Long): Date {
        return Date.from(
            Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(days)).instant()
        )
    }

    const val firstPage = 0
    const val emptySize = 0
    const val defaultSize = 30
}
