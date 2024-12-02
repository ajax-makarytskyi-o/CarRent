package com.makarytskyi.rentcar.user.infrastructure.rest.dto

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val city: String,
)
