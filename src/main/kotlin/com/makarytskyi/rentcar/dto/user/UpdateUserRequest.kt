package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:NotBlank
    val name: String,
    @field:Size(max = 15)
    val phoneNumber: String?,
    val city: String?,
) {

    fun toEntity(): User = User(
        name = name,
        email = null,
        phoneNumber = phoneNumber,
        city = city,
    )
}
