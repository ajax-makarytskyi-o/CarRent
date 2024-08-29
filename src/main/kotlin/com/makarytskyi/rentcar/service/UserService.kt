package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.repository.UserRepository
import org.springframework.stereotype.Service

@Service
internal class UserService(private val userRepository: UserRepository) {

    fun findAll(): List<UserResponse> = userRepository.findAll().map { UserResponse.from(it) }

    fun create(createUserRequest: CreateUserRequest): UserResponse {
        validateAvailabilityEmail(createUserRequest.email)
        createUserRequest.phoneNumber?.let { validateAvailabilityPhoneNumber(createUserRequest.phoneNumber) }
        return UserResponse.from(userRepository.create(CreateUserRequest.toEntity(createUserRequest)))
    }

    fun getById(id: String): UserResponse = userRepository.findById(id)?.let { UserResponse.from(it) }
        ?: throw ResourceNotFoundException("User with id $id is not found")

    fun deleteById(id: String) = userRepository.deleteById(id)

    fun update(id: String, userRequest: UpdateUserRequest): UserResponse {
        userRequest.phoneNumber?.let { validateAvailabilityPhoneNumber(it) }

        return userRepository.update(id, UpdateUserRequest.toEntity(userRequest))
            ?.let { UserResponse.from(it) }
            ?: throw ResourceNotFoundException("User with id $id is not found")
    }

    fun getByEmail(email: String): UserResponse = userRepository.findByEmail(email)?.let { UserResponse.from(it) }
        ?: throw ResourceNotFoundException("User with email $email is not found")

    fun getByPhoneNumber(phoneNumber: String): UserResponse =
        userRepository.findByPhoneNumber(phoneNumber)?.let { UserResponse.from(it) }
            ?: throw ResourceNotFoundException("User with phone number $phoneNumber is not found")

    private fun validateAvailabilityEmail(email: String) {
        require(userRepository.findByEmail(email) == null) { "User with email $email is already exist" }
    }

    private fun validateAvailabilityPhoneNumber(phoneNumber: String) {
        require(userRepository.findByPhoneNumber(phoneNumber) == null)
        { "User with phone number $phoneNumber is already exist" }
    }
}
