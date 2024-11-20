package com.makarytskyi.gateway.mapper

import com.makarytskyi.commonmodels.user.User
import com.makarytskyi.core.dto.user.UserResponseDto

fun User.toResponse(): UserResponseDto = UserResponseDto(
    id = id,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    city = city,
)
