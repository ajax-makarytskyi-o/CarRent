package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.user.UserResponseDto
import com.makarytskyi.internalapi.commonmodels.user.User
import com.makarytskyi.rentcar.model.MongoUser

fun UserResponseDto.toProto(): User = User.newBuilder()
    .apply {
        setId(this@toProto.id)
        setName(this@toProto.name)
        setEmail(this@toProto.email)
        setPhoneNumber(this@toProto.phoneNumber)
        setCity(this@toProto.city)
    }
    .build()

fun MongoUser.toResponse(): UserResponseDto = UserResponseDto(
    id = id?.toString().orEmpty(),
    name = name.orEmpty(),
    email = email.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    city = city.orEmpty(),
)
