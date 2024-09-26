package com.makarytskyi.rentcar.repository

import fixtures.UserFixture.randomUser
import fixtures.UserFixture.unexistingUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UserRepositoryTests : ContainerBase {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `create should insert user and return it with id`() {
        // GIVEN
        val unexistingUser = unexistingUser()

        // WHEN
        val user = userRepository.create(unexistingUser)

        // THEN
        val foundUser = userRepository.findById(user.id.toString())
        assertNotNull(foundUser)
        assertNotNull(user.id)
    }

    @Test
    fun `findAll should find all users`() {
        // GIVEN
        val insertedUser1 = userRepository.create(randomUser())
        val insertedUser2 = userRepository.create(randomUser())

        // WHEN
        val allUsers = userRepository.findAll()

        // THEN
        assertTrue(allUsers.any { it.email == insertedUser1.email && it.name == insertedUser1.name })
        assertTrue(allUsers.any { it.email == insertedUser2.email && it.name == insertedUser2.name })
    }

    @Test
    fun `update should update phone number and name of user`() {
        // GIVEN
        val name = "Alex"
        val phoneNumber = "670185014"

        val user = userRepository.create(randomUser())

        val updateUser = user.copy(
            name = name,
            phoneNumber = phoneNumber,
            city = null
        )

        // WHEN
        userRepository.update(updateUser.id.toString(), updateUser)

        // THEN
        val updatedUser = userRepository.findById(updateUser.id.toString())

        assertNotNull(updatedUser)
        assertEquals(name, updatedUser?.name)
        assertEquals(phoneNumber, updatedUser?.phoneNumber)
        assertEquals(user.city, updatedUser?.city)
    }

    @Test
    fun `update should update city of user`() {
        // GIVEN
        val city = "London"
        val user = userRepository.create(randomUser())

        val updateUser = user.copy(
            name = null,
            phoneNumber = null,
            city = city
        )

        // WHEN
        userRepository.update(user.id.toString(), updateUser)

        // THEN
        val updated = userRepository.findById(user.id.toString())

        assertNotNull(updated)
        assertEquals(city, updated?.city)
        assertEquals(user.name, updated?.name)
        assertEquals(user.phoneNumber, updated?.phoneNumber)
    }

    @Test
    fun `findByPhoneNumber should return user found by phone number`() {
        // GIVEN
        val phoneNumber = "15025025"
        userRepository.create(randomUser().copy(phoneNumber = phoneNumber))

        // WHEN
        val foundUser = userRepository.findByPhoneNumber(phoneNumber)

        // THEN
        assertNotNull(foundUser)
        assertEquals(phoneNumber, foundUser?.phoneNumber)
    }

    @Test
    fun `findByPhoneNumber should return null if cant find by phone number`() {
        // GIVEN
        val unexistingPhoneNumber = "00000000"

        // WHEN
        val user = userRepository.findByPhoneNumber(unexistingPhoneNumber)

        // THEN
        assertNull(user)
    }

    @Test
    fun `findByEmail should return user found by email`() {
        // GIVEN
        val email = "testing@email.com"
        userRepository.create(randomUser().copy(email = email))

        // WHEN
        val foundUser = userRepository.findByEmail(email)

        // THEN
        assertNotNull(foundUser)
        assertEquals(email, foundUser?.email)
    }

    @Test
    fun `findByEmail should return null if cant find by email`() {
        // GIVEN
        val unexistingEmail = "wrong@email.com"

        // WHEN
        val foundUser = userRepository.findByEmail(unexistingEmail)

        // THEN
        assertNull(foundUser)
    }

    @Test
    fun `findById should return user if found by id`() {
        // GIVEN
        val user = userRepository.create(randomUser())

        // WHEN
        val foundUser = userRepository.findById(user.id.toString())

        // THEN
        assertNotNull(foundUser)
    }

    @Test
    fun `findById should return null if cant find user by id`() {
        // GIVEN
        val unexistingId = "wrongId"

        // WHEN
        val foundUser = userRepository.findById(unexistingId)

        // THEN
        assertNull(foundUser)
    }

    @Test
    fun `deleteById should delete user by id`() {
        // GIVEN
        val user = userRepository.create(randomUser())

        val resultBefore = userRepository.findById(user.id.toString())

        // WHEN
        userRepository.deleteById(user.id.toString())

        // THEN
        val result = userRepository.findById(user.id.toString())

        assertNotNull(resultBefore)
        assertNull(result)
    }
}
