package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.User
import org.springframework.stereotype.Repository

@Repository
internal interface UserRepository {

    fun create(user: User): User

    fun findById(id: String): User?

    fun findAll(): List<User>

    fun deleteById(id: String)

    fun update(id: String, user: User): User?

    fun findByPhoneNumber(phoneNumber: String): User?

    fun findByEmail(email: String): User?
}
