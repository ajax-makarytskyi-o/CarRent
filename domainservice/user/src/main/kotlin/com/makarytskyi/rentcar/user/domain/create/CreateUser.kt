package com.makarytskyi.rentcar.user.domain.create

data class CreateUser(
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val city: String?,
)
