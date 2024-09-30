package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.MongoUser
import fixtures.UserFixture.createUserRequest
import fixtures.UserFixture.existingUser
import fixtures.UserFixture.updateUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserDTOTests {
    @Test
    fun `response mapper should return response successfully`() {
        // GIVEN
        val user = existingUser()
        val response = UserResponse(
            id = user.id.toString(),
            name = user.name!!,
            email = user.email!!,
            phoneNumber = user.phoneNumber!!,
            city = user.city!!,
        )

        // WHEN
        val result = UserResponse.from(user)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `response mapper return response with default fields if user fields are null`() {
        // GIVEN
        val user = MongoUser(
            id = null,
            name = null,
            email = null,
            phoneNumber = null,
            city = null,
        )
        val response = UserResponse(
            id = "",
            name = "",
            email = "",
            phoneNumber = "",
            city = "",
        )

        // WHEN
        val result = UserResponse.from(user)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `create request mapper should return entity successfully`() {
        // GIVEN
        val request = createUserRequest()
        val entity = MongoUser(
            id = null,
            name = request.name,
            email = request.email,
            phoneNumber = request.phoneNumber,
            city = request.city,
        )

        // WHEN
        val result = CreateUserRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }

    @Test
    fun `create request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = createUserRequest().copy(
            phoneNumber = null,
            city = null,
        )

        val entity = MongoUser(
            id = null,
            name = request.name,
            email = request.email,
            phoneNumber = null,
            city = null,
        )

        // WHEN
        val result = CreateUserRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }

    @Test
    fun `update request mapper should return entity successfully`() {
        // GIVEN
        val request = updateUserRequest()
        val entity = MongoUser(
            id = null,
            name = request.name,
            email = null,
            phoneNumber = request.phoneNumber,
            city = request.city,
        )

        // WHEN
        val result = UpdateUserRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }

    @Test
    fun `update request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = updateUserRequest().copy(
            name = null,
            phoneNumber = null,
            city = null,
        )
        val entity = MongoUser(
            id = null,
            name = null,
            email = null,
            phoneNumber = null,
            city = null,
        )

        // WHEN
        val result = UpdateUserRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }
}
