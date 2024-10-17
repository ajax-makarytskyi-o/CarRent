package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal interface UserService {

    fun findAll(page: Int, size: Int): Flux<UserResponse>

    fun create(createUserRequest: CreateUserRequest): Mono<UserResponse>

    fun getById(id: String): Mono<UserResponse>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, userRequest: UpdateUserRequest): Mono<UserResponse>

    fun getByEmail(email: String): Mono<UserResponse>

    fun getByPhoneNumber(phoneNumber: String): Mono<UserResponse>
}
