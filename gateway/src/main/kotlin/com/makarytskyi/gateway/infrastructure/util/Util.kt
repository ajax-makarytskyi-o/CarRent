package com.makarytskyi.gateway.infrastructure.util

import com.google.protobuf.Timestamp
import java.util.Date

object Util {
    fun timestampToDate(timestamp: Timestamp): Date = Date(timestamp.seconds.times(MILLIS_IN_SECOND))

    fun dateToTimestamp(date: Date): Timestamp =
        Timestamp.newBuilder().setSeconds(date.time.div(MILLIS_IN_SECOND)).build()

    private const val MILLIS_IN_SECOND = 1000
}
