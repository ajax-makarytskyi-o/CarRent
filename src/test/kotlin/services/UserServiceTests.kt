package services

import UserFixture.createUserEntity
import UserFixture.createUserRequest
import UserFixture.createdUser
import UserFixture.createdUserResponse
import UserFixture.existingUser
import UserFixture.responseUser
import UserFixture.updateUserEntity
import UserFixture.updateUserRequest
import UserFixture.updatedUser
import UserFixture.userId
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.User
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.UserService
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
class UserServiceTests {
    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userService: UserService

    @Test
    fun `getById should return UserResponse when User exists`() {
        //GIVEN
        whenever(userRepository.findById(userId)).thenReturn(existingUser)

        //WHEN
        val result = userService.getById(userId)

        //THEN
        assertEquals(responseUser, result)
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
        val users: List<User> = listOf(existingUser)
        val expected = listOf(responseUser)
        whenever(userRepository.findAll()).thenReturn(users)

        //WHEN
        val result = userService.findAll()

        //THEN
        verify(userRepository).findAll()
        assertEquals(expected, result)
    }

    @Test
    fun `findAll should return empty List of UserResponse if repository return empty List`() {
        //GIVEN
        whenever(userRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = userService.findAll()

        //THEN
        verify(userRepository).findAll()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `should create user successfully`() {
        //GIVEN
        whenever(userRepository.create(createUserEntity)).thenReturn(createdUser)

        //WHEN
        val result = userService.create(createUserRequest)

        //THEN
        verify(userRepository).create(createUserEntity)
        assertEquals(createdUserResponse, result)
    }

    @Test
    fun `update should return updated user`() {
        //GIVEN
        whenever(userRepository.update(userId, updateUserEntity)).thenReturn(updatedUser)

        //WHEN
        val result = userService.update(userId, updateUserRequest)

        //THEN
        assertNotNull(result)
        verify(userRepository).update(userId, updateUserEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if user is not found`() {
        //GIVEN
        val userId = "unknown"

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { userService.update(userId, updateUserRequest) })
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
