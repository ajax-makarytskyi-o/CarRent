package com.makarytskyi.rentcar.order.infrastructure.nats.mapper

import com.makarytskyi.commonmodels.user.User
import com.makarytskyi.rentcar.user.domain.DomainUser

fun DomainUser.toProto(): User = User.newBuilder()
    .also {
        it.setId(id)
        it.setName(name)
        it.setEmail(email)
        it.setPhoneNumber(phoneNumber)
        it.setCity(city)
    }
    .build()
