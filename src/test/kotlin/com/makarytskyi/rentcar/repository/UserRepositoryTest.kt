package com.makarytskyi.rentcar.repository

import fixtures.UserFixture.emptyUserPatch
import fixtures.UserFixture.randomUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class UserRepositoryTest : ContainerBase {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `create should insert user and return it with id`() {
        // GIVEN
        val unexistingUser = randomUser()

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
        val allUsers = userRepository.findAll(0, 20)

        // THEN
        assertTrue(allUsers.any { it.email == insertedUser1.email && it.name == insertedUser1.name })
        assertTrue(allUsers.any { it.email == insertedUser2.email && it.name == insertedUser2.name })
    }

    @Test
    fun `patch should partially update user`() {
        // GIVEN
        val name = "Alex"
        val phoneNumber = "670185014"
        val city = "London"

        val user = userRepository.create(randomUser())

        val updateUser = emptyUserPatch().copy(
            name = name,
            phoneNumber = phoneNumber,
            city = city
        )

        // WHEN
        val updated = userRepository.patch(user.id.toString(), updateUser)

        // THEN
        assertEquals(name, updated?.name)
        assertEquals(phoneNumber, updated?.phoneNumber)
        assertEquals(city, updated?.city)
    }

    @Test
    fun `findByPhoneNumber should return user found by phone number`() {
        // GIVEN
        val phoneNumber = "15025025"
        val user = userRepository.create(randomUser().copy(phoneNumber = phoneNumber))

        // WHEN
        val foundUser = userRepository.findByPhoneNumber(phoneNumber)

        // THEN
        assertEquals(user, foundUser)
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
        val user = userRepository.create(randomUser().copy(email = email))

        // WHEN
        val foundUser = userRepository.findByEmail(email)

        // THEN
        assertEquals(user, foundUser)
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
        assertEquals(user, foundUser)
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

        // WHEN
        userRepository.deleteById(user.id.toString())

        // THEN
        val result = userRepository.findById(user.id.toString())

        assertNull(result)
    }
}
