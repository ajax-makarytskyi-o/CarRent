package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.user.UserResponse

data class User(
    val id: String? = null,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?,
) {

    companion object {
        fun toResponse(user: User): UserResponse = UserResponse(
            user.id!!,
            user.name ?: "none",
            user.email ?: "none",
            user.phoneNumber ?: "none",
            user.city ?: "none",
        )
    }
}
