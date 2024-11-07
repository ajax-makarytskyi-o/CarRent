package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.dto.user.UserResponseDto
import com.makarytskyi.internalapi.commonmodels.user.User

fun User.toResponse(): UserResponseDto = UserResponseDto(
    id = id,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    city = city,
)
