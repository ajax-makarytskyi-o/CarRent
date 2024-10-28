package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.dto.user.UserResponse
import com.makarytskyi.internalapi.model.user.User

fun User.toResponse(): UserResponse = UserResponse(
    id = id,
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    city = city,
)
