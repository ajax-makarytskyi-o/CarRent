package com.makarytskyi.rentcar.user.infrastructure.rest.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateUserRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String,
    @field:Pattern(regexp = "\\d{4,15}")
    val phoneNumber: String?,
    val city: String?,
)
