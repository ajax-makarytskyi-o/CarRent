package com.makarytskyi.core.fixtures

import java.time.Clock
import java.time.Duration
import java.util.Date

internal object Utils {
    internal fun getDateFromNow(days: Long): Date {
        return Date.from(
            Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(days)).instant()
        )
    }
}
