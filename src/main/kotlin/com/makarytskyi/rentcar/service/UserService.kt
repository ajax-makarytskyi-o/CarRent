package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.exception.UserNotFoundException
import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.impl.InMemoryUserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: InMemoryUserRepository) {

    fun findAll(): List<UserResponse> = userRepository.findAll().map { User.toResponse(it) }

    fun create(createUserRequest: CreateUserRequest): UserResponse {
        validateAvailabilityEmail(createUserRequest.email)
        createUserRequest.phoneNumber?.let { validateAvailabilityPhoneNumber(createUserRequest.phoneNumber) }
        return User.toResponse(userRepository.create(CreateUserRequest.toEntity(createUserRequest)))
    }

    fun getById(id: String): UserResponse = userRepository.findById(id)?.let { User.toResponse(it) }
        ?: throw UserNotFoundException("User with id $id is not found")

    fun deleteById(id: String) = userRepository.deleteById(id)

    fun update(id: String, userRequest: UpdateUserRequest): UserResponse {
        validateNotEmptyRequest(userRequest)
        userRequest.phoneNumber?.let { validateAvailabilityPhoneNumber(it) }

        return userRepository.update(id, UpdateUserRequest.toEntity(userRequest))
            ?.let { User.toResponse(it) }
            ?: throw UserNotFoundException("User with id $id is not found")
    }

    fun getByEmail(email: String): UserResponse = userRepository.findByEmail(email)?.let { User.toResponse(it) }
        ?: throw UserNotFoundException("User with email $email is not found")

    fun getByPhoneNumber(phoneNumber: String): UserResponse =
        userRepository.findByPhoneNumber(phoneNumber)?.let { User.toResponse(it) }
            ?: throw UserNotFoundException("User with phone number $phoneNumber is not found")

    private fun validateAvailabilityEmail(email: String) {
        require(userRepository.findByEmail(email) == null) { "User with email $email is already exist" }
    }

    private fun validateAvailabilityPhoneNumber(phoneNumber: String) {
        require(userRepository.findByPhoneNumber(phoneNumber) == null) { "User with phone number $phoneNumber is already exist" }
    }

    private fun validateNotEmptyRequest(userRequest: UpdateUserRequest) {
        require(userRequest.city != null || userRequest.name != null || userRequest.phoneNumber != null) { "Update request is empty" }
    }
}
