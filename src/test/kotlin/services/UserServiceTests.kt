package services

import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.impl.UserServiceImpl
import fixtures.UserFixture.createUserEntity
import fixtures.UserFixture.createUserRequest
import fixtures.UserFixture.createdUser
import fixtures.UserFixture.existingUser
import fixtures.UserFixture.responseUser
import fixtures.UserFixture.updateUserEntity
import fixtures.UserFixture.updateUserRequest
import fixtures.UserFixture.updatedUser
import fixtures.UserFixture.userId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class UserServiceTests {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userService: UserServiceImpl

    @Test
    fun `getById should return UserResponse when User exists`() {
        //GIVEN
        val user = existingUser()
        val response = responseUser(user)
        whenever(userRepository.findById(userId)).thenReturn(user)

        //WHEN
        val result = userService.getById(userId)

        //THEN
        assertEquals(response, result)
        verify(userRepository).findById(userId)
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        //GIVEN
        whenever(userRepository.findById(userId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { userService.getById(userId) })
        verify(userRepository).findById(userId)
    }

    @Test
    fun `findAll should return List of UserResponse`() {
        //GIVEN
        val user = existingUser()
        val response = responseUser(user)
        val users: List<User> = listOf(user)
        val expected = listOf(response)
        whenever(userRepository.findAll()).thenReturn(users)

        //WHEN
        val result = userService.findAll()

        //THEN
        assertEquals(expected, result)
        verify(userRepository).findAll()
    }

    @Test
    fun `findAll should return empty List of UserResponse if repository return empty List`() {
        //GIVEN
        whenever(userRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = userService.findAll()

        //THEN
        assertEquals(emptyList(), result)
        verify(userRepository).findAll()
    }

    @Test
    fun `should create user successfully`() {
        //GIVEN
        val request = createUserRequest()
        val createUserEntity = createUserEntity(request)
        val createdUser = createdUser(createUserEntity)
        val response = responseUser(createdUser)
        whenever(userRepository.create(createUserEntity)).thenReturn(createdUser)

        //WHEN
        val result = userService.create(request)

        //THEN
        assertEquals(response, result)
        verify(userRepository).create(createUserEntity)
    }

    @Test
    fun `update should return updated user`() {
        //GIVEN
        val user = existingUser()
        val request = updateUserRequest()
        val requestEntity = updateUserEntity(request)
        val updatedUser = updatedUser(user, request)
        whenever(userRepository.update(userId, requestEntity)).thenReturn(updatedUser)

        //WHEN
        val result = userService.update(userId, request)

        //THEN
        assertNotNull(result)
        verify(userRepository).update(userId, requestEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if user is not found`() {
        //GIVEN
        val userId = "unknown"

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { userService.update(userId, updateUserRequest()) })
    }

    @Test
    fun `deleteById should not throw ResourceNotFoundException if user is not found`() {
        //GIVEN
        val userId = "unknown"

        //WHEN //THEN
        assertNotNull(userService.deleteById(userId))
        verify(userRepository).deleteById(userId)
    }
}
