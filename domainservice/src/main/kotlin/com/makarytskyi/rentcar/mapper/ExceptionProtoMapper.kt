package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.error.ExceptionType

fun Throwable.toProto(): ExceptionType =
    when (this) {
        is NotFoundException -> ExceptionType.NOT_FOUND
        is IllegalArgumentException -> ExceptionType.ILLEGAL_ARGUMENT
        else -> ExceptionType.EXCEPTION
    }
