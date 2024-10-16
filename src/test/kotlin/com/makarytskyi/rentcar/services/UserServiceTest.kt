package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserEntity
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.createdUser
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.UserFixture.responseUser
import com.makarytskyi.rentcar.fixtures.UserFixture.updateUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.updatedUser
import com.makarytskyi.rentcar.fixtures.UserFixture.userPatch
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.impl.UserServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
internal class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var userService: UserServiceImpl

    @Test
    fun `getById should return user response when user exists`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)
        every { userRepository.findById(user.id.toString()) }.returns(user.toMono())

        // WHEN
        val result = userService.getById(user.id.toString())

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { userRepository.findById(user.id.toString()) }
    }

    @Test
    fun `getById should return throw NotFoundException`() {
        // GIVEN
        val userId = ObjectId()
        every { userRepository.findById(userId.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        userService.getById(userId.toString())
            .test()
            .verifyError(NotFoundException::class.java)

        verify { userRepository.findById(userId.toString()) }
    }

    @Test
    fun `findAll should return user responses`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)
        every { userRepository.findAll(0, 10) }.returns(Flux.just(user))

        // WHEN
        val result = userService.findAll(0, 10)

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertTrue(it.contains(response), "Result should contain expected user response.")
            }
            .verifyComplete()

        verify { userRepository.findAll(0, 10) }
    }

    @Test
    fun `findAll should return empty if repository returned empty`() {
        // GIVEN
        every { userRepository.findAll(0, 10) }.returns(Flux.empty())

        // WHEN
        val result = userService.findAll(0, 10)

        // THEN
        result
            .test()
            .verifyComplete()

        verify { userRepository.findAll(0, 10) }
    }

    @Test
    fun `should create user successfully`() {
        // GIVEN
        val request = createUserRequest()
        val createUserEntity = createUserEntity(request)
        val createdUser = createdUser(createUserEntity)
        val response = responseUser(createdUser)
        every { userRepository.create(createUserEntity) }.returns(createdUser.toMono())

        // WHEN
        val result = userService.create(request)

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { userRepository.create(createUserEntity) }
    }

    @Test
    fun `patch should return updated user`() {
        // GIVEN
        val user = randomUser()
        val request = updateUserRequest()
        val requestEntity = userPatch(request)
        val updatedUser = updatedUser(user, request)
        val response = responseUser(updatedUser)
        every { userRepository.patch(user.id.toString(), requestEntity) }.returns(updatedUser.toMono())

        // WHEN
        val result = userService.patch(user.id.toString(), request)

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { userRepository.patch(user.id.toString(), requestEntity) }
    }

    @Test
    fun `patch should throw NotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"
        val request = updateUserRequest()
        every { userRepository.patch(userId, userPatch(request)) }.returns(Mono.empty())

        // WHEN // THEN
        userService.patch(userId, request)
            .test()
            .verifyError(NotFoundException::class.java)

        verify { userRepository.patch(userId, userPatch(request)) }
    }

    @Test
    fun `deleteById should not throw NotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"
        every { userRepository.deleteById(userId) }.returns(Mono.empty())

        // WHEN // THEN
        userService.deleteById(userId)
            .test()
            .verifyComplete()

        verify { userRepository.deleteById(userId) }
    }

    @Test
    fun `patch should throw IllegalArgumentException if user with specified phone number already exists`() {
        // GIVEN
        val user = randomUser()
        val request = updateUserRequest()
        every { userRepository.patch(user.id.toString(), userPatch(request)) }
            .returns(DuplicateKeyException("key email is duplicated").toMono())

        // WHEN // THEN
        userService.patch(user.id.toString(), request)
            .test()
            .verifyError(IllegalArgumentException::class.java)

        verify { userRepository.patch(user.id.toString(), userPatch(request)) }
    }
}
