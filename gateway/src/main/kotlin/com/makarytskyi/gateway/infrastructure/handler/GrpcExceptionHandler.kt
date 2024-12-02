package com.makarytskyi.gateway.infrastructure.handler

import com.makarytskyi.core.exception.NotFoundException
import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler

@GrpcAdvice
class GrpcExceptionHandler {
    @GrpcExceptionHandler
    fun handleInvalidArgument(e: IllegalArgumentException): StatusException {
        return Status.INVALID_ARGUMENT.withDescription(e.message).withCause(e).asException()
    }

    @GrpcExceptionHandler(NotFoundException::class)
    fun handleResourceNotFoundException(e: NotFoundException): StatusException {
        val status: Status = Status.NOT_FOUND.withDescription(e.message).withCause(e);
        return status.asException();
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleException(e: Exception): StatusException {
        val status: Status = Status.INTERNAL.withDescription(e.message).withCause(e);
        return status.asException();
    }
}
