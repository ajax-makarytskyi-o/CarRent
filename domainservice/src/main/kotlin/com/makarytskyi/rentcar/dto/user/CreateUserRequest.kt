package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.MongoUser
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateUserRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String,
    @field:Pattern(regexp = "\\d{4,15}")
    val phoneNumber: String?,
    val city: String?,
) {

    companion object {
        fun toEntity(userRequest: CreateUserRequest): MongoUser = MongoUser(
            name = userRequest.name,
            email = userRequest.email,
            phoneNumber = userRequest.phoneNumber,
            city = userRequest.city,
        )
    }
}
