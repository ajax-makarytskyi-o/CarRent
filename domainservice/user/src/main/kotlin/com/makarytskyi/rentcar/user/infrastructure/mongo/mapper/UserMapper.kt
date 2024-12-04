package com.makarytskyi.rentcar.user.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.create.CreateUser
import com.makarytskyi.rentcar.user.infrastructure.mongo.entity.MongoUser

fun MongoUser.toDomain(): DomainUser = DomainUser(
    id = this.id.toString(),
    name = requireNotNull(this.name) { "Name of user is null" },
    email = requireNotNull(this.email) { "Email of user is null" },
    phoneNumber = this.phoneNumber,
    city = this.city,
)

fun CreateUser.toMongo(): MongoUser = MongoUser(
    name = this.name,
    email = this.email,
    phoneNumber = this.phoneNumber,
    city = this.city,
)
