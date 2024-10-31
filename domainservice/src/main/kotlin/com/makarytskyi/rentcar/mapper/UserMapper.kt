package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.user.UserResponseDto
import com.makarytskyi.internalapi.commonmodels.user.User
import com.makarytskyi.rentcar.model.MongoUser

fun UserResponseDto.toProto(): User = User.newBuilder()
    .also {
        it.setId(this.id)
        it.setName(this.name)
        it.setEmail(this.email)
        it.setPhoneNumber(this.phoneNumber)
        it.setCity(this.city)
    }
    .build()

fun MongoUser.toResponse(): UserResponseDto = UserResponseDto(
    id = requireNotNull(id?.toString()) { "User id is null" },
    name = name.orEmpty(),
    email = email.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    city = city.orEmpty(),
)
