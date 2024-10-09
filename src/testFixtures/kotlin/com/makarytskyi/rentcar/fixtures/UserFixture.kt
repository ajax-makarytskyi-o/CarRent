package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.fixtures.Utils.generateString
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoUserPatch
import org.bson.types.ObjectId

object UserFixture {
    fun responseUser(mongoUser: MongoUser) = UserResponse(
        id = mongoUser.id.toString(),
        name = mongoUser.name!!,
        email = mongoUser.email!!,
        phoneNumber = mongoUser.phoneNumber!!,
        city = mongoUser.city!!,
    )

    fun emptyResponseUser() = UserResponse(
        id = "",
        name = "",
        email = "",
        phoneNumber = "",
        city = "",
    )

    fun createUserRequest() = CreateUserRequest(
        name = generateString(10),
        email = "${generateString(8)}@gmail.com",
        phoneNumber = generateString(10),
        city = generateString(8),
    )

    fun createUserEntity(request: CreateUserRequest) = MongoUser(
        id = null,
        name = request.name,
        email = request.email,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun createdUser(mongoUser: MongoUser) = mongoUser.copy(id = ObjectId())

    fun updateUserRequest() = UpdateUserRequest(
        name = generateString(10),
        phoneNumber = generateString(10),
        city = generateString(8)
    )

    fun userPatch(request: UpdateUserRequest) = MongoUserPatch(
        name = request.name,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun emptyUserPatch() = MongoUserPatch(
        name = null,
        phoneNumber = null,
        city = null,
    )

    fun randomUser() = MongoUser(
        id = ObjectId(),
        name = generateString(10),
        email = "${generateString(8)}@gmail.com",
        phoneNumber = generateString(10),
        city = generateString(8),
    )

    fun updatedUser(mongoUser: MongoUser, request: UpdateUserRequest) =
        mongoUser.copy(name = request.name, phoneNumber = request.phoneNumber, city = request.city)
}
