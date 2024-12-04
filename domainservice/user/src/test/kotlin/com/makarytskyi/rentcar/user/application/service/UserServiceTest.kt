package com.makarytskyi.rentcar.user.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.fixtures.UserFixture.createdUser
import com.makarytskyi.rentcar.fixtures.UserFixture.domainUserPatch
import com.makarytskyi.rentcar.fixtures.UserFixture.domainUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.UserFixture.responseUser
import com.makarytskyi.rentcar.fixtures.UserFixture.updateUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.updatedUser
import com.makarytskyi.rentcar.fixtures.UserFixture.userPatch
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
internal class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepositoryOutputPort

    @InjectMockKs
    lateinit var userService: UserService

    @Test
    fun `getById should return user response when user exists`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)
        every { userRepository.findById(user.id.toString()) } returns user.toMono()

        // WHEN
        val result = userService.getById(user.id.toString()).map { it.toResponse() }

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { userRepository.findById(user.id.toString()) }
    }

    @Test
    fun `getById should return NotFoundException`() {
        // GIVEN
        val userId = ObjectId()
        every { userRepository.findById(userId.toString()) } returns Mono.empty()

        // WHEN // THEN
        userService.getById(userId.toString())
            .test()
            .verifyError<NotFoundException>()

        verify { userRepository.findById(userId.toString()) }
    }

    @Test
    fun `findAll should return user responses`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)
        val users = listOf(user)
        every { userRepository.findAll(0, 10) } returns users.toFlux()

        // WHEN
        val result = userService.findAll(0, 10).map { it.toResponse() }

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(response)
            }
            .verifyComplete()

        verify { userRepository.findAll(0, 10) }
    }

    @Test
    fun `findAll should return empty if repository returned empty`() {
        // GIVEN
        every { userRepository.findAll(0, 10) } returns Flux.empty()

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
        val request = domainUserRequest()
        val createdUser = createdUser(request)
        val response = responseUser(createdUser)
        every { userRepository.create(request) } returns createdUser.toMono()

        // WHEN
        val result = userService.create(request).map { it.toResponse() }

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { userRepository.create(request) }
    }

    @Test
    fun `patch should return updated user`() {
        // GIVEN
        val user = randomUser()
        val patch = userPatch(updateUserRequest())
        val request = domainUserPatch(patch, user)
        val updatedUser = updatedUser(user, patch)
        val response = responseUser(updatedUser)
        every { userRepository.findById(user.id.toString()) } returns user.toMono()
        every { userRepository.patch(user.id.toString(), request) } returns updatedUser.toMono()

        // WHEN
        val result = userService.patch(user.id.toString(), patch).map { it.toResponse() }

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { userRepository.patch(user.id.toString(), request) }
    }

    @Test
    fun `patch should return NotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"
        val request = userPatch(updateUserRequest())
        every { userRepository.findById(userId) } returns Mono.empty()

        // WHEN // THEN
        userService.patch(userId, request)
            .test()
            .verifyError<NotFoundException>()

        verify { userRepository.findById(userId) }
    }

    @Test
    fun `deleteById should not return NotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"
        every { userRepository.deleteById(userId) } returns Mono.empty()

        // WHEN // THEN
        userService.deleteById(userId)
            .test()
            .verifyComplete()

        verify { userRepository.deleteById(userId) }
    }

    @Test
    fun `patch should return IllegalArgumentException if user with specified phone number already exists`() {
        // GIVEN
        val user = randomUser()
        val patch = userPatch(updateUserRequest())
        val request = domainUserPatch(patch, user)
        every { userRepository.findById(user.id.toString()) } returns user.toMono()
        every { userRepository.patch(user.id.toString(), request) } returns
                DuplicateKeyException("key email is duplicated").toMono()

        // WHEN // THEN
        userService.patch(user.id.toString(), patch)
            .test()
            .verifyError<IllegalArgumentException>()

        verify { userRepository.patch(user.id.toString(), request) }
    }
}
