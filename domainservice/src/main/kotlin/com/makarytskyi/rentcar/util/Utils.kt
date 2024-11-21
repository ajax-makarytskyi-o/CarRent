package com.makarytskyi.rentcar.util

import com.google.protobuf.Timestamp
import java.util.Date

object Utils {
    fun dateToTimestamp(date: Date): Timestamp =
        Timestamp.newBuilder().setSeconds(date.time.div(MILLIS_IN_SECOND)).build()

    fun timestampToDate(timestamp: Timestamp): Date = Date(timestamp.seconds.times(MILLIS_IN_SECOND))

    private const val MILLIS_IN_SECOND = 1000
}
