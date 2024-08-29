package com.makarytskyi.rentcar.exception

import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
internal class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    internal fun handleResourceNotFoundException(): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    internal fun handleIllegalArgumentException(): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    internal data class ErrorResponse(
        val timestamp: LocalDateTime,
        val status: Int,
    )
}
