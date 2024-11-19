package com.makarytskyi.rentcar.util

import com.google.protobuf.Timestamp
import java.util.Date

fun dateToTimestamp(date: Date): Timestamp =
    Timestamp.newBuilder().setSeconds(date.time).build()

fun timestampToDate(timestamp: Timestamp): Date = Date(timestamp.seconds)
