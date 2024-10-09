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
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.impl.UserServiceImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userService: UserServiceImpl

    @Test
    fun `getById should return UserResponse when User exists`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)
        whenever(userRepository.findById(user.id.toString())).thenReturn(user)

        // WHEN
        val result = userService.getById(user.id.toString())

        // THEN
        assertEquals(response, result)
        verify(userRepository).findById(user.id.toString())
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val userId = ObjectId()
        whenever(userRepository.findById(userId.toString())).thenReturn(null)

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { userService.getById(userId.toString()) })
        verify(userRepository).findById(userId.toString())
    }

    @Test
    fun `findAll should return List of UserResponse`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)
        val mongoUsers: List<MongoUser> = listOf(user)
        val expected = listOf(response)
        whenever(userRepository.findAll(0, 10)).thenReturn(mongoUsers)

        // WHEN
        val result = userService.findAll(0, 10)

        // THEN
        assertEquals(expected, result)
        verify(userRepository).findAll(0, 10)
    }

    @Test
    fun `findAll should return empty List of UserResponse if repository return empty List`() {
        // GIVEN
        whenever(userRepository.findAll(0, 10)).thenReturn(emptyList())

        // WHEN
        val result = userService.findAll(0, 10)

        // THEN
        assertEquals(emptyList(), result)
        verify(userRepository).findAll(0, 10)
    }

    @Test
    fun `should create user successfully`() {
        // GIVEN
        val request = createUserRequest()
        val createUserEntity = createUserEntity(request)
        val createdUser = createdUser(createUserEntity)
        val response = responseUser(createdUser)
        whenever(userRepository.create(createUserEntity)).thenReturn(createdUser)

        // WHEN
        val result = userService.create(request)

        // THEN
        assertEquals(response, result)
        verify(userRepository).create(createUserEntity)
    }

    @Test
    fun `patch should return updated user`() {
        // GIVEN
        val user = randomUser()
        val request = updateUserRequest()
        val requestEntity = userPatch(request)
        val updatedUser = updatedUser(user, request)
        whenever(userRepository.patch(user.id.toString(), requestEntity)).thenReturn(updatedUser)

        // WHEN
        val result = userService.patch(user.id.toString(), request)

        // THEN
        assertNotNull(result)
        verify(userRepository).patch(user.id.toString(), requestEntity)
    }

    @Test
    fun `patch should throw ResourceNotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { userService.patch(userId, updateUserRequest()) })
    }

    @Test
    fun `deleteById should not throw ResourceNotFoundException if user is not found`() {
        // GIVEN
        val userId = "unknown"

        // WHEN // THEN
        assertNotNull(userService.deleteById(userId))
        verify(userRepository).deleteById(userId)
    }

    @Test
    fun `patch should throw IllegalArgumentException if user with specified phone number already exists`() {
        // GIVEN
        val user = randomUser()
        val request = updateUserRequest()
        whenever(userRepository.findByPhoneNumber(request.phoneNumber!!)).thenReturn(randomUser())

        // WHEN // THEN
        assertThrows(IllegalArgumentException::class.java, { userService.patch(user.id.toString(), request) })
    }
}
