package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.MongoUser

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val city: String,
) {

    companion object {
        fun from(mongoUser: MongoUser): UserResponse = UserResponse(
            requireNotNull(mongoUser.id?.toString()) { "User id is null" },
            mongoUser.name.orEmpty(),
            mongoUser.email.orEmpty(),
            mongoUser.phoneNumber.orEmpty(),
            mongoUser.city.orEmpty(),
        )
    }
}
