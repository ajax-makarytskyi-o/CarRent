package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import kotlin.time.measureTime

@Repository
class SimpleUserRepository: UserRepository {
    val map: MutableMap<String, User> = HashMap()

    override fun save(user: User): User {
        val id = ObjectId().toString()
        val savedUser = User(
            id,
            user.name,
            user.email,
            user.phoneNumber,
            user.city
        )

        map[id] = savedUser
        return savedUser
    }

    override fun findById(id: String?): User?  {
        return map[id]
    }

    override fun findAll(): List<User> = map.values.toList()

    override fun deleteById(id: String) = map.remove(id)

    override fun update(id: String, user: User): User? {
        val oldUser: User? = findById(id)

        if (oldUser == null) {
            return null
        } else {
            val updatedUser: User = User(
                id,
                name = user.name ?: oldUser.name,
                email = user.email ?: oldUser.email,
                phoneNumber = user.phoneNumber ?: oldUser.phoneNumber,
                city = user.city ?: oldUser.city
            )
            map[id] = updatedUser
            return updatedUser
        }
    }

    override fun findByPhoneNumber(phoneNumber: String): User? = map.values.find { it.phoneNumber == phoneNumber }

    override fun findByEmail(email: String) = map.values.find { it.email == email }
}
