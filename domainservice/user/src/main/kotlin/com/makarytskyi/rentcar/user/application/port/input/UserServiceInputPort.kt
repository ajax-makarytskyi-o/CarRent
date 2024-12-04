package com.makarytskyi.rentcar.user.application.port.input

import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.create.CreateUser
import com.makarytskyi.rentcar.user.domain.patch.PatchUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserServiceInputPort {
    fun findAll(page: Int, size: Int): Flux<DomainUser>

    fun create(user: CreateUser): Mono<DomainUser>

    fun getById(id: String): Mono<DomainUser>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, patch: PatchUser): Mono<DomainUser>

    fun getByEmail(email: String): Mono<DomainUser>

    fun getByPhoneNumber(phoneNumber: String): Mono<DomainUser>
}
