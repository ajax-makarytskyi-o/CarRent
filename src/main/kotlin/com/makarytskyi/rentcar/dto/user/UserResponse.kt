package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.User

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val city: String,
) {

    companion object {
        fun from(user: User): UserResponse = UserResponse(
            user.id!!,
            user.name ?: "none",
            user.email ?: "none",
            user.phoneNumber ?: "none",
            user.city ?: "none",
        )
    }
}
