package com.makarytskyi.rentcar.user.application.port.output

import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.create.CreateUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepositoryOutputPort {
    fun create(user: CreateUser): Mono<DomainUser>

    fun findById(id: String): Mono<DomainUser>

    fun findAll(page: Int, size: Int): Flux<DomainUser>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, patch: DomainUser): Mono<DomainUser>

    fun findByPhoneNumber(phoneNumber: String): Mono<DomainUser>

    fun findByEmail(email: String): Mono<DomainUser>
}
