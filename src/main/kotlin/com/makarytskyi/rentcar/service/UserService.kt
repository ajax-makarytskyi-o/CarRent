package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.exception.UserNotFoundException
import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.impl.SimpleUserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: SimpleUserRepository) {

    fun findAll(): List<UserResponse> = userRepository.findAll().map { it.toResponse() }

    fun save(createUserRequest: CreateUserRequest): UserResponse {
        userRepository.findByEmail(createUserRequest.email)?.let { throw IllegalArgumentException("User with email ${createUserRequest.email} is already exist") }

        createUserRequest.phoneNumber?.let { userRepository.findByPhoneNumber(it) }?.let { throw IllegalArgumentException("User with phone number ${createUserRequest.phoneNumber} is already exist") }

        return userRepository.save(createUserRequest.toEntity()).toResponse()
    }

    fun findById(id: String): UserResponse = userRepository.findById(id)?.toResponse()
        ?: throw UserNotFoundException("User with id $id is not found")

    fun deleteById(id: String) {
        userRepository.deleteById(id)
    }

    fun update(id: String, userRequest: UpdateUserRequest): UserResponse {
        userRequest.phoneNumber?.let { userRepository.findByPhoneNumber(it) }?.let { throw IllegalArgumentException("User with phone number ${userRequest.phoneNumber} is already exist") }

        return userRepository.update(id, userRequest.toEntity())?.toResponse()
            ?: throw UserNotFoundException("User with id $id is not found")
    }

    fun findByEmail(email: String): UserResponse = userRepository.findByEmail(email)?.toResponse()
        ?: throw UserNotFoundException("User with email $email is not found")

    fun findByPhoneNumber(phoneNumber: String): UserResponse =
        userRepository.findByPhoneNumber(phoneNumber)?.toResponse()
            ?: throw UserNotFoundException("User with phone number $phoneNumber is not found")
}
