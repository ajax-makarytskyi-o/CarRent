package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:Pattern(regexp = "^\\S.*")
    val name: String?,
    @field:Pattern(regexp = "\\d{4,15}")
    val phoneNumber: String?,
    val city: String?,
) {

    companion object {
        fun toEntity(userRequest: UpdateUserRequest): User = User(
            name = userRequest.name,
            email = null,
            phoneNumber = userRequest.phoneNumber,
            city = userRequest.city,
        )
    }
}
