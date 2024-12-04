package com.makarytskyi.rentcar.user.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.common.annotation.InvocationTracker
import com.makarytskyi.rentcar.user.application.port.input.UserServiceInputPort
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.create.CreateUser
import com.makarytskyi.rentcar.user.domain.patch.PatchUser
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@InvocationTracker
@Service
class UserService(private val userOutputPort: UserRepositoryOutputPort) : UserServiceInputPort {
    override fun findAll(page: Int, size: Int): Flux<DomainUser> =
        userOutputPort.findAll(page, size)

    override fun create(user: CreateUser): Mono<DomainUser> {
        return userOutputPort.create(user)
            .onErrorMap(DuplicateKeyException::class.java) {
                IllegalArgumentException("User with this phone number or email already exists")
            }
    }

    override fun getById(id: String): Mono<DomainUser> = userOutputPort.findById(id)
        .switchIfEmpty { Mono.error(NotFoundException("User with id $id is not found")) }

    override fun deleteById(id: String): Mono<Unit> = userOutputPort.deleteById(id)

    override fun patch(id: String, patch: PatchUser): Mono<DomainUser> {
        return userOutputPort.findById(id)
            .flatMap { userOutputPort.patch(id, it.fromPatch(patch)) }
            .onErrorMap(DuplicateKeyException::class.java) {
                IllegalArgumentException("User with phone number ${patch.phoneNumber} already exists")
            }
            .switchIfEmpty { Mono.error(NotFoundException("User with id $id is not found")) }
    }

    override fun getByEmail(email: String): Mono<DomainUser> = userOutputPort.findByEmail(email)
        .switchIfEmpty { Mono.error(NotFoundException("User with email $email is not found")) }

    override fun getByPhoneNumber(phoneNumber: String): Mono<DomainUser> =
        userOutputPort.findByPhoneNumber(phoneNumber)
            .switchIfEmpty { Mono.error(NotFoundException("User with phone number $phoneNumber is not found")) }
}
