package com.makarytskyi.rentcar.fixtures

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.Duration
import java.util.Date
import kotlin.random.Random

internal object Utils {
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

    fun randomPrice(): BigDecimal {
        val min = BigDecimal.ZERO
        val max = BigDecimal("5000")
        val range = max.subtract(min)
        val randomFraction = BigDecimal(Random.nextDouble())
        val randomValue = min.add(range.multiply(randomFraction))
        return randomValue.setScale(2, RoundingMode.HALF_UP)
    }
}
