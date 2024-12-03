package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.fixtures.Utils.generateString
import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.patch.DomainUserPatch
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.CreateUserRequest
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.UpdateUserRequest
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.UserResponse
import org.bson.types.ObjectId

object UserFixture {
    fun randomName(): String {
        return generateString(10)
    }

    fun randomEmail(): String {
        return "${generateString(8)}@gmail.com"
    }

    fun randomPhoneNumber(): String {
        return generateString(10)
    }

    fun randomCity(): String {
        return generateString(8)
    }

    fun responseUser(user: DomainUser) = UserResponse(
        id = user.id.toString(),
        name = user.name,
        email = user.email,
        phoneNumber = user.phoneNumber!!,
        city = user.city!!,
    )

    fun createUserRequest() = CreateUserRequest(
        name = randomName(),
        email = randomEmail(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun domainUserRequest() = DomainUser(
        id = null,
        name = randomName(),
        email = randomEmail(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun createUserEntity(request: CreateUserRequest) = DomainUser(
        id = null,
        name = request.name,
        email = request.email,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun createdUser(user: DomainUser) = user.copy(id = ObjectId().toString())

    fun updateUserRequest() = UpdateUserRequest(
        name = randomName(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun userPatch(request: UpdateUserRequest) = DomainUserPatch(
        name = request.name,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun emptyUserPatch() = DomainUserPatch(
        name = null,
        phoneNumber = null,
        city = null,
    )

    fun randomUser() = DomainUser(
        id = ObjectId().toString(),
        name = randomName(),
        email = randomEmail(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun domainUserPatch(patch: DomainUserPatch, oldUser: DomainUser) = oldUser.copy(
        name = patch.name ?: oldUser.name,
        phoneNumber = patch.phoneNumber ?: oldUser.phoneNumber,
        city = patch.city ?: oldUser.city,
    )

    fun updatedUser(user: DomainUser, patch: DomainUserPatch) =
        user.copy(
            name = patch.name ?: user.name,
            phoneNumber = patch.phoneNumber ?: user.phoneNumber,
            city = patch.city ?: user.city,
        )
}
