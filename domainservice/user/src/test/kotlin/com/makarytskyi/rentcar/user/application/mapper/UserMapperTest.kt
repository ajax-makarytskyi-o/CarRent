package com.makarytskyi.rentcar.user.application.mapper

import com.makarytskyi.rentcar.fixtures.UserFixture.randomCity
import com.makarytskyi.rentcar.fixtures.UserFixture.randomName
import com.makarytskyi.rentcar.fixtures.UserFixture.randomPhoneNumber
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.user.domain.patch.PatchUser
import kotlin.test.Test
import kotlin.test.assertEquals

class UserMapperTest {

    @Test
    fun `patch mapper should return user with updated fields`() {
        // GIVEN
        val user = randomUser()

        val patch = PatchUser(
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

        val patch = PatchUser(
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
