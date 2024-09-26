package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.MongoUser
import jakarta.validation.constraints.Pattern

data class UpdateUserRequest(
    @field:Pattern(regexp = "^\\S.*")
    val name: String?,
    @field:Pattern(regexp = "\\d{4,15}")
    val phoneNumber: String?,
    val city: String?,
) {

    companion object {
        fun toEntity(userRequest: UpdateUserRequest): MongoUser = MongoUser(
            name = userRequest.name,
            email = null,
            phoneNumber = userRequest.phoneNumber,
            city = userRequest.city,
        )
    }
}
