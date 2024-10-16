package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.fixtures.UserFixture.emptyUserPatch
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

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
        StepVerifier.create(user)
            .assertNext {
                assertNotNull(it.id)
                assertEquals(unexistingUser.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all users`() {
        // GIVEN
        val insertedUser1 = userRepository.create(randomUser()).block()
        val insertedUser2 = userRepository.create(randomUser()).block()

        // WHEN
        val allUsers = userRepository.findAll(0, 20)

        // THEN
        StepVerifier.create(allUsers.collectList())
            .assertNext {
                assertTrue(it.containsAll(listOf(insertedUser1, insertedUser2)))
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update user`() {
        // GIVEN
        val name = "Alex"
        val phoneNumber = "670185014"
        val city = "London"

        val user = userRepository.create(randomUser()).block()

        val updateUser = emptyUserPatch().copy(
            name = name,
            phoneNumber = phoneNumber,
            city = city
        )

        // WHEN
        val updated = userRepository.patch(user?.id.toString(), updateUser)

        // THEN
        StepVerifier.create(updated)
            .assertNext {
                assertEquals(name, it.name)
                assertEquals(phoneNumber, it.phoneNumber)
                assertEquals(city, it.city)
            }
            .verifyComplete()
    }

    @Test
    fun `findByPhoneNumber should return user found by phone number`() {
        // GIVEN
        val phoneNumber = "15025025"
        val user = userRepository.create(randomUser().copy(phoneNumber = phoneNumber)).block()

        // WHEN
        val foundUser = userRepository.findByPhoneNumber(phoneNumber)

        // THEN
        StepVerifier.create(foundUser)
            .assertNext {
                assertEquals(user, it)
                assertEquals(phoneNumber, it.phoneNumber)
            }
            .verifyComplete()
    }

    @Test
    fun `findByPhoneNumber should not return anything if cant find by phone number`() {
        // GIVEN
        val unexistingPhoneNumber = "00000000"

        // WHEN
        val user = userRepository.findByPhoneNumber(unexistingPhoneNumber)

        // THEN
        StepVerifier.create(user)
            .verifyComplete()
    }

    @Test
    fun `findByEmail should return user found by email`() {
        // GIVEN
        val email = "testing@email.com"
        val user = userRepository.create(randomUser().copy(email = email)).block()

        // WHEN
        val foundUser = userRepository.findByEmail(email)

        // THEN
        StepVerifier.create(foundUser)
            .assertNext {
                assertEquals(user, it)
                assertEquals(email, it.email)
            }
            .verifyComplete()
    }

    @Test
    fun `findByEmail should not return anything if cant find by email`() {
        // GIVEN
        val unexistingEmail = "wrong@email.com"

        // WHEN
        val foundUser = userRepository.findByEmail(unexistingEmail)

        // THEN
        StepVerifier.create(foundUser)
            .verifyComplete()
    }

    @Test
    fun `findById should return user if found by id`() {
        // GIVEN
        val user = userRepository.create(randomUser()).block()

        // WHEN
        val foundUser = userRepository.findById(user?.id.toString())

        // THEN
        StepVerifier.create(foundUser)
            .assertNext {
                assertEquals(user, it)
            }
            .verifyComplete()
    }

    @Test
    fun `findById should not return anything if cant find user by id`() {
        // GIVEN
        val unexistingId = "wrongId"

        // WHEN
        val foundUser = userRepository.findById(unexistingId)

        // THEN
        StepVerifier.create(foundUser)
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete user by id`() {
        // GIVEN
        val user = userRepository.create(randomUser()).block()

        // WHEN
        userRepository.deleteById(user?.id.toString()).block()

        // THEN
        StepVerifier.create(userRepository.findById(user?.id.toString()))
            .verifyComplete()
    }
}
