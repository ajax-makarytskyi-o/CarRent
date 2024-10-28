package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserEntity
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.emptyResponseUser
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.UserFixture.responseUser
import com.makarytskyi.rentcar.fixtures.UserFixture.updateUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.userPatch
import com.makarytskyi.rentcar.model.MongoUser
import kotlin.test.Test
import kotlin.test.assertEquals

class UserMapperTest {
    @Test
    fun `response mapper should return response successfully`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)

        // WHEN
        val result = UserResponse.from(user)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `response mapper return response with default fields if user fields are null`() {
        // GIVEN
        val user = MongoUser()
        val response = emptyResponseUser()

        // WHEN
        val result = UserResponse.from(user)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `create request mapper should return entity successfully`() {
        // GIVEN
        val request = createUserRequest()
        val entity = createUserEntity(request)

        // WHEN
        val result = CreateUserRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `create request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = createUserRequest().copy(
            phoneNumber = null,
            city = null,
        )

        val entity = createUserEntity(request)

        // WHEN
        val result = CreateUserRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request mapper should return entity successfully`() {
        // GIVEN
        val request = updateUserRequest()
        val entity = userPatch(request)

        // WHEN
        val result = UpdateUserRequest.toPatch(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = updateUserRequest().copy(
            name = null,
            phoneNumber = null,
            city = null,
        )
        val entity = userPatch(request)

        // WHEN
        val result = UpdateUserRequest.toPatch(request)

        // THEN
        assertEquals(entity, result)
    }
}
