package com.makarytskyi.rentcar.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
internal class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    internal fun handleResourceNotFoundException(): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    internal fun handleIllegalArgumentException(): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    internal data class ErrorResponse(
        val status: Int,
    )
}
