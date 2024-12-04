package com.makarytskyi.rentcar.user.infrastructure.rest.mapper

import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.create.CreateUser
import com.makarytskyi.rentcar.user.domain.patch.PatchUser
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.CreateUserRequest
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.UpdateUserRequest
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.UserResponse

fun CreateUserRequest.toDomain(): CreateUser = CreateUser(
    name = this.name,
    email = this.email,
    phoneNumber = this.phoneNumber,
    city = this.city,
)

fun UpdateUserRequest.toPatch() = PatchUser(
    name = this.name,
    phoneNumber = this.phoneNumber,
    city = this.city,
)

fun DomainUser.toResponse(): UserResponse = UserResponse(
    requireNotNull(this.id) { "User id is null" },
    this.name,
    this.email,
    this.phoneNumber.orEmpty(),
    this.city.orEmpty(),
)
