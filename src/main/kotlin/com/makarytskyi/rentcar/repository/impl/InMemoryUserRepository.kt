package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class InMemoryUserRepository: UserRepository {
    val map: MutableMap<String, User> = HashMap()

    override fun create(user: User): User {
        val id = ObjectId().toHexString()
        val savedUser = user.copy(id = id)
        map[id] = savedUser
        return savedUser
    }

    override fun findById(id: String): User? = map[id]

    override fun findAll(): List<User> = map.values.toList()

    override fun deleteById(id: String) {
        map.remove(id)
    }

    override fun update(id: String, user: User): User? {
        val oldUser: User? = findById(id)

        return oldUser?.let {
            val updatedUser = oldUser.copy(
                name = user.name,
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
