package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.User

data class CreateUserRequest(
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?
) {

    fun toEntity(): User = User(
        null,
        name ?: throw IllegalArgumentException("Name of user is null"),
        email ?: throw IllegalArgumentException("Email of user is null"),
        phoneNumber ?: throw IllegalArgumentException("Phone number of user is null"),
        city
    )
}
