package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.user.UserResponse

data class User(
    val id: String? = null,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?,
) {

    fun toResponse(): UserResponse = UserResponse(
        id ?: "none",
        name ?: "none",
        email ?: "none",
        phoneNumber ?: "none",
        city ?: "none",
    )
}
