package com.makarytskyi.rentcar.dto.user

import com.makarytskyi.rentcar.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String,
    @field:Size(max = 15)
    val phoneNumber: String?,
    val city: String?,
) {

    fun toEntity(): User = User(
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        city = city,
    )
}
