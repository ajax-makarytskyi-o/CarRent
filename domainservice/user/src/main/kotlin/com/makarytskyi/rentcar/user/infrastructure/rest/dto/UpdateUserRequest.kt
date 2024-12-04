package com.makarytskyi.rentcar.user.infrastructure.rest.dto

import jakarta.validation.constraints.Pattern

data class UpdateUserRequest(
    @field:Pattern(regexp = "^\\S.*")
    val name: String?,
    @field:Pattern(regexp = "\\d{4,15}")
    val phoneNumber: String?,
    val city: String?,
)
