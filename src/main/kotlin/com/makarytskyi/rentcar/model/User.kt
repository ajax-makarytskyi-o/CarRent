package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.user.UserResponse

data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?
) {

    fun toResponse(): UserResponse = UserResponse(
        id,
        name ?: throw IllegalArgumentException("Name of user is null"),
        email ?: throw IllegalArgumentException("Email of user is null"),
        phoneNumber ?: throw IllegalArgumentException("Phone number of user is null"),
        city
    )
}
