package com.makarytskyi.rentcar.mapper

import com.makarytskyi.commonmodels.user.User
import com.makarytskyi.core.dto.user.UserResponseDto
import com.makarytskyi.rentcar.model.MongoUser

fun UserResponseDto.toProto(): User = User.newBuilder()
    .also {
        it.setId(id)
        it.setName(name)
        it.setEmail(email)
        it.setPhoneNumber(phoneNumber)
        it.setCity(city)
    }
    .build()

fun MongoUser.toResponse(): UserResponseDto = UserResponseDto(
    id = requireNotNull(id?.toString()) { "User id is null" },
    name = name.orEmpty(),
    email = email.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    city = city.orEmpty(),
)
