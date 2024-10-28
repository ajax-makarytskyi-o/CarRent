package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.user.UserResponse
import com.makarytskyi.internalapi.model.user.User
import com.makarytskyi.rentcar.model.MongoUser

fun UserResponse.toProto(): User = User.newBuilder()
    .setId(this.id)
    .setName(this.name)
    .setEmail(this.email)
    .setPhoneNumber(this.phoneNumber)
    .setCity(this.city)
    .build()

fun MongoUser.toResponse(): UserResponse = UserResponse(
    id = id?.toString().orEmpty(),
    name = name.orEmpty(),
    email = email.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    city = city.orEmpty(),
)
