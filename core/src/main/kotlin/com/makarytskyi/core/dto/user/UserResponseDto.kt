package com.makarytskyi.core.dto.user

data class UserResponseDto(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val city: String,
)
