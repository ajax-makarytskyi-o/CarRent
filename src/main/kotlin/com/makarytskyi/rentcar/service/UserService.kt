package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse

internal interface UserService {

    fun findAll(): List<UserResponse>

    fun create(createUserRequest: CreateUserRequest): UserResponse

    fun getById(id: String): UserResponse

    fun deleteById(id: String)

    fun update(id: String, userRequest: UpdateUserRequest): UserResponse

    fun getByEmail(email: String): UserResponse

    fun getByPhoneNumber(phoneNumber: String): UserResponse
}
