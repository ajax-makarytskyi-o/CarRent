package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.UserService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@InvocationTracker
@Service
internal class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun findAll(page: Int, size: Int): Flux<UserResponse> =
        userRepository.findAll(page, size).map { UserResponse.from(it) }

    override fun create(createUserRequest: CreateUserRequest): Mono<UserResponse> {
        return validateAvailabilityEmail(createUserRequest.email)
            .switchIfEmpty(
                Mono.justOrEmpty(createUserRequest.phoneNumber).flatMap { validateAvailabilityPhoneNumber(it) }
            )
            .switchIfEmpty(Mono.defer { userRepository.create(CreateUserRequest.toEntity(createUserRequest)) })
            .map { UserResponse.from(it) }
    }

    override fun getById(id: String): Mono<UserResponse> = userRepository.findById(id)
        .switchIfEmpty(Mono.error(NotFoundException("User with id $id is not found")))
        .map { UserResponse.from(it) }

    override fun deleteById(id: String): Mono<Unit> = userRepository.deleteById(id)

    override fun patch(id: String, userRequest: UpdateUserRequest): Mono<UserResponse> {
        return Mono.justOrEmpty(userRequest.phoneNumber)
            .flatMap { validateAvailabilityPhoneNumber(it) }
            .switchIfEmpty(Mono.defer { userRepository.patch(id, UpdateUserRequest.toPatch(userRequest)) })
            .switchIfEmpty(Mono.error(NotFoundException("User with id $id is not found")))
            .map { UserResponse.from(it) }
    }

    override fun getByEmail(email: String): Mono<UserResponse> = userRepository.findByEmail(email)
        .switchIfEmpty(Mono.error(NotFoundException("User with email $email is not found")))
        .map { UserResponse.from(it) }

    override fun getByPhoneNumber(phoneNumber: String): Mono<UserResponse> =
        userRepository.findByPhoneNumber(phoneNumber)
            .switchIfEmpty(Mono.error(NotFoundException("User with phone number $phoneNumber is not found")))
            .map { UserResponse.from(it) }

    private fun validateAvailabilityEmail(email: String): Mono<MongoUser> {
        return userRepository.findByEmail(email)
            .flatMap { Mono.error(IllegalArgumentException("User with email $email already exists")) }
    }

    private fun validateAvailabilityPhoneNumber(phoneNumber: String): Mono<MongoUser> {
        return userRepository.findByPhoneNumber(phoneNumber)
            .flatMap { Mono.error(IllegalArgumentException("User with phone number $phoneNumber already exists")) }
    }
}
