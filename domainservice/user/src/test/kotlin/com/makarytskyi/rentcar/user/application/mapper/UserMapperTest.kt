package com.makarytskyi.rentcar.user.application.mapper

import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserEntity
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.randomCity
import com.makarytskyi.rentcar.fixtures.UserFixture.randomName
import com.makarytskyi.rentcar.fixtures.UserFixture.randomPhoneNumber
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.UserFixture.responseUser
import com.makarytskyi.rentcar.fixtures.UserFixture.updateUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.userPatch
import com.makarytskyi.rentcar.user.domain.patch.DomainUserPatch
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toDomain
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toPatch
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId

class UserMapperTest {
    @Test
    fun `response mapper should return response successfully`() {
        // GIVEN
        val user = randomUser()
        val response = responseUser(user)

        // WHEN
        val result = user.toResponse()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `create request mapper should return entity successfully`() {
        // GIVEN
        val request = createUserRequest()
        val entity = createUserEntity(request)

        // WHEN
        val result = request.toDomain()

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
        val result = request.toDomain()

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request mapper should return entity successfully`() {
        // GIVEN
        val request = updateUserRequest()
        val entity = userPatch(request)

        // WHEN
        val result = request.toPatch()

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
        val result = request.toPatch()

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `patch mapper should return user with updated fields`() {
        // GIVEN
        val user = randomUser()

        val patch = DomainUserPatch(
            name = randomName(),
            phoneNumber = randomPhoneNumber(),
            city = randomCity(),
        )

        val expected = user.copy(name = patch.name!!, phoneNumber = patch.phoneNumber, city = patch.city)

        // WHEN
        val result = user.fromPatch(patch)

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun `patch mapper should return old user if patch is empty`() {
        // GIVEN
        val user = randomUser()

        val patch = DomainUserPatch(
            name = null,
            phoneNumber = null,
            city = null,
        )

        // WHEN
        val result = user.fromPatch(patch)

        // THEN
        assertEquals(user, result)
    }
}
