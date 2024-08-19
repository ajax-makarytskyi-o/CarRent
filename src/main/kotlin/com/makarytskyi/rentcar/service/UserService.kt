package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.exception.UserNotFoundException
import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.impl.SimpleUserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: SimpleUserRepository) {

    fun findAll(): List<UserResponse> = userRepository.findAll().map { it.toResponse() }

    fun save(createUserRequest: CreateUserRequest): UserResponse {
        val user: User = createUserRequest.toEntity()

        if (userRepository.findByEmail(user.email ?: throw IllegalArgumentException("User doesn't have email")) != null)
            throw IllegalArgumentException("User with email ${user.email} is already exist")

        if (userRepository.findByPhoneNumber(user.phoneNumber ?: throw IllegalArgumentException("User doesn't have phone number")) != null)
            throw IllegalArgumentException("User with phone number ${user.phoneNumber} is already exist")

        return userRepository.save(user).toResponse()
    }

    fun findById(id: String): UserResponse = userRepository.findById(id)?.toResponse()
        ?: throw UserNotFoundException("User with id $id is not found")

    fun deleteById(id: String): UserResponse = userRepository.deleteById(id)?.toResponse()
        ?: throw UserNotFoundException("User with id $id is not found")

    fun update(id: String, userRequest: UpdateUserRequest): UserResponse {
        if (userRequest.phoneNumber?.let { userRepository.findByPhoneNumber(it) } != null)
            throw IllegalArgumentException("User with phone number ${userRequest.phoneNumber} is already exist")

        return userRepository.update(id, userRequest.toEntity())?.toResponse()
            ?: throw UserNotFoundException("User with id $id is not found")
    }

    fun findByEmail(email: String): UserResponse = userRepository.findByEmail(email)?.toResponse()
        ?: throw UserNotFoundException("User with email $email is not found")

    fun findByPhoneNumber(phoneNumber: String): UserResponse = userRepository.findByPhoneNumber(phoneNumber)?.toResponse()
            ?: throw UserNotFoundException("User with phone number $phoneNumber is not found")
}
