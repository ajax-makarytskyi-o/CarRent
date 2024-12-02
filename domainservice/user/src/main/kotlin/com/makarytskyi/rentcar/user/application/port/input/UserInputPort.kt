package com.makarytskyi.rentcar.user.application.port.input

import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.patch.DomainUserPatch
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserInputPort {
    fun findAll(page: Int, size: Int): Flux<DomainUser>

    fun create(createUserRequest: DomainUser): Mono<DomainUser>

    fun getById(id: String): Mono<DomainUser>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, userRequest: DomainUserPatch): Mono<DomainUser>

    fun getByEmail(email: String): Mono<DomainUser>

    fun getByPhoneNumber(phoneNumber: String): Mono<DomainUser>
}
