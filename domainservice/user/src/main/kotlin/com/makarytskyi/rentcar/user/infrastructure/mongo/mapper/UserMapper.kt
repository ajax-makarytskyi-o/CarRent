package com.makarytskyi.rentcar.user.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.infrastructure.mongo.entity.MongoUser
import org.bson.types.ObjectId

fun MongoUser.toDomain(): DomainUser = DomainUser(
    id = this.id.toString(),
    name = this.name ?: throw IllegalArgumentException("Name of user is null"),
    email = this.email ?: throw IllegalArgumentException("Email of user is null"),
    phoneNumber = this.phoneNumber,
    city = this.city,
)

fun DomainUser.toMongo(): MongoUser = MongoUser(
    id = this.id?.let { ObjectId(it) },
    name = this.name,
    email = this.email,
    phoneNumber = this.phoneNumber,
    city = this.city,
)
