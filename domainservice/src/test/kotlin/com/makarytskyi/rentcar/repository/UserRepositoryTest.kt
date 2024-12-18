package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.fixtures.UserFixture.emptyUserPatch
import com.makarytskyi.rentcar.fixtures.UserFixture.randomCity
import com.makarytskyi.rentcar.fixtures.UserFixture.randomEmail
import com.makarytskyi.rentcar.fixtures.UserFixture.randomName
import com.makarytskyi.rentcar.fixtures.UserFixture.randomPhoneNumber
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.Utils.defaultSize
import com.makarytskyi.rentcar.fixtures.Utils.firstPage
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

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
        user
            .test()
            .assertNext {
                assertNotNull(it.id, "User should have non-null id after saving")
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
        val allUsers = userRepository.findAll(firstPage, defaultSize)

        // THEN
        allUsers.collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(listOf(insertedUser1, insertedUser2))
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update user`() {
        // GIVEN
        val name = randomName()
        val phoneNumber = randomPhoneNumber()
        val city = randomCity()

        val user = userRepository.create(randomUser()).block()!!

        val updateUser = emptyUserPatch().copy(
            name = name,
            phoneNumber = phoneNumber,
            city = city
        )

        // WHEN
        val updated = userRepository.patch(user.id.toString(), updateUser)

        // THEN
        updated
            .test()
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
        val phoneNumber = randomPhoneNumber()
        val user = userRepository.create(randomUser().copy(phoneNumber = phoneNumber)).block()!!

        // WHEN
        val foundUser = userRepository.findByPhoneNumber(phoneNumber)

        // THEN
        foundUser
            .test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `findByPhoneNumber should return empty if cant find by phone number`() {
        // GIVEN
        val unexistingPhoneNumber = randomPhoneNumber()

        // WHEN
        val user = userRepository.findByPhoneNumber(unexistingPhoneNumber)

        // THEN
        user
            .test()
            .verifyComplete()
    }

    @Test
    fun `findByEmail should return user found by email`() {
        // GIVEN
        val email = randomEmail()
        val user = userRepository.create(randomUser().copy(email = email)).block()!!

        // WHEN
        val foundUser = userRepository.findByEmail(email)

        // THEN
        foundUser
            .test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `findByEmail should return empty if cant find by email`() {
        // GIVEN
        val unexistingEmail = randomEmail()

        // WHEN
        val foundUser = userRepository.findByEmail(unexistingEmail)

        // THEN
        foundUser
            .test()
            .verifyComplete()
    }

    @Test
    fun `findById should return user if found by id`() {
        // GIVEN
        val user = userRepository.create(randomUser()).block()!!

        // WHEN
        val foundUser = userRepository.findById(user.id.toString())

        // THEN
        foundUser
            .test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty if cant find user by id`() {
        // GIVEN
        val unexistingId = ObjectId().toString()

        // WHEN
        val foundUser = userRepository.findById(unexistingId)

        // THEN
        foundUser
            .test()
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete user by id`() {
        // GIVEN
        val user = userRepository.create(randomUser()).block()!!

        // WHEN
        userRepository.deleteById(user.id.toString()).block()

        // THEN
        userRepository.findById(user.id.toString())
            .test()
            .verifyComplete()
    }
}
