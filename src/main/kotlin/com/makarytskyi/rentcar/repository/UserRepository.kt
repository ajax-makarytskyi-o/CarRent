package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoUser
import org.springframework.stereotype.Repository

@Repository
internal interface UserRepository {

    fun create(mongoUser: MongoUser): MongoUser

    fun findById(id: String): MongoUser?

    fun findAll(): List<MongoUser>

    fun deleteById(id: String)

    fun update(id: String, mongoUser: MongoUser): MongoUser?

    fun findByPhoneNumber(phoneNumber: String): MongoUser?

    fun findByEmail(email: String): MongoUser?
}
