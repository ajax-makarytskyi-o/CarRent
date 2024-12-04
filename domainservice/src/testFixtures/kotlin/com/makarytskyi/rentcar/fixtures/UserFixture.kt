package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.fixtures.Utils.generateString
import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.create.CreateUser
import com.makarytskyi.rentcar.user.domain.patch.PatchUser
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
        id = user.id,
        name = user.name,
        email = user.email,
        phoneNumber = user.phoneNumber!!,
        city = user.city!!,
    )

    fun createUserRequest() = CreateUser(
        name = randomName(),
        email = randomEmail(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun createUserRequestDto() = CreateUserRequest(
        name = randomName(),
        email = randomEmail(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun domainUserRequest() = CreateUser(
        name = randomName(),
        email = randomEmail(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun createUserEntity(request: CreateUserRequest) = CreateUser(
        name = request.name,
        email = request.email,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun createdUser(user: CreateUser): DomainUser = DomainUser(
        id = ObjectId().toString(),
        name = user.name,
        email = user.email,
        phoneNumber = user.phoneNumber,
        city = user.city,
    )

    fun updateUserRequest() = UpdateUserRequest(
        name = randomName(),
        phoneNumber = randomPhoneNumber(),
        city = randomCity(),
    )

    fun userPatch(request: UpdateUserRequest) = PatchUser(
        name = request.name,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun emptyUserPatch() = PatchUser(
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

    fun domainUserPatch(patch: PatchUser, oldUser: DomainUser) = oldUser.copy(
        name = patch.name ?: oldUser.name,
        phoneNumber = patch.phoneNumber ?: oldUser.phoneNumber,
        city = patch.city ?: oldUser.city,
    )

    fun updatedUser(user: DomainUser, patch: PatchUser) =
        user.copy(
            name = patch.name ?: user.name,
            phoneNumber = patch.phoneNumber ?: user.phoneNumber,
            city = patch.city ?: user.city,
        )
}
