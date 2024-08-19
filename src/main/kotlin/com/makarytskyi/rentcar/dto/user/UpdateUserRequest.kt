package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.User

data class UpdateUserRequest(
    val name: String?,
    val phoneNumber: String?,
    val city: String?
) {

    fun toEntity(): User = User(
        null,
        name,
        null,
        phoneNumber,
        city
    )
}
