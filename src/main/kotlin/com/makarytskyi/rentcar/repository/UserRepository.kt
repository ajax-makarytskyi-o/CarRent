package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoUserPatch
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal interface UserRepository {

    fun create(mongoUser: MongoUser): Mono<MongoUser>

    fun findById(id: String): Mono<MongoUser>

    fun findAll(page: Int, size: Int): Flux<MongoUser>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, userPatch: MongoUserPatch): Mono<MongoUser>

    fun findByPhoneNumber(phoneNumber: String): Mono<MongoUser>

    fun findByEmail(email: String): Mono<MongoUser>
}
