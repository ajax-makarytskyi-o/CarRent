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
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
        every { userRepository.findById(user.id.toString()) }.returns(Mono.just(user))

        // WHEN
        val result = userService.getById(user.id.toString())

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
            .verifyComplete()

        verify { userRepository.findById(user.id.toString()) }
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val userId = ObjectId()
        every { userRepository.findById(userId.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(userService.getById(userId.toString()))
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
        StepVerifier.create(result.collectList())
            .assertNext {
                assertTrue(it.contains(response))
            }
            .verifyComplete()

        verify { userRepository.findAll(0, 10) }
    }

    @Test
    fun `findAll should not return anything if repository didn't return anything`() {
        // GIVEN
        every { userRepository.findAll(0, 10) }.returns(Flux.empty())

        // WHEN
        val result = userService.findAll(0, 10)

        // THEN
        StepVerifier.create(result)
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
        every { userRepository.findByEmail(request.email) }.returns(Mono.empty())
        every { userRepository.findByPhoneNumber(request.phoneNumber!!) }.returns(Mono.empty())
        every { userRepository.create(createUserEntity) }.returns(Mono.just(createdUser))

        // WHEN
        val result = userService.create(request)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
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
        every { userRepository.patch(user.id.toString(), requestEntity) }.returns(Mono.just(updatedUser))
        every { userRepository.findByPhoneNumber(request.phoneNumber!!) }.returns(Mono.empty())

        // WHEN
        val result = userService.patch(user.id.toString(), request)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
            .verifyComplete()

        verify { userRepository.patch(user.id.toString(), requestEntity) }
    }

    @Test
    fun `patch should throw ResourceNotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"
        val request = updateUserRequest()
        every { userRepository.findByPhoneNumber(request.phoneNumber!!) }.returns(Mono.empty())
        every { userRepository.patch(userId, userPatch(request)) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(userService.patch(userId, request))
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `deleteById should not throw ResourceNotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"
        every { userRepository.deleteById(userId) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(userService.deleteById(userId))
            .verifyComplete()

        verify { userRepository.deleteById(userId) }
    }

    @Test
    fun `patch should throw IllegalArgumentException if user with specified phone number already exists`() {
        // GIVEN
        val user = randomUser()
        val request = updateUserRequest()
        every { userRepository.findByPhoneNumber(request.phoneNumber!!) }.returns(Mono.just(user))

        // WHEN // THEN
        StepVerifier.create(userService.patch(user.id.toString(), request))
            .verifyError(IllegalArgumentException::class.java)
    }
}
