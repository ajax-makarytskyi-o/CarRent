package com.makarytskyi.rentcar.order.infrastructure.nats.mapper

import com.makarytskyi.commonmodels.user.User
import com.makarytskyi.rentcar.user.domain.DomainUser

fun DomainUser.toProto(): User = User.newBuilder()
    .also {
        it.id = id
        it.name = name
        it.email = email
        it.phoneNumber = phoneNumber ?: ""
        it.city = city ?: ""
    }
    .build()
