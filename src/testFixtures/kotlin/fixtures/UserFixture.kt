package fixtures

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.MongoUser
import kotlin.random.Random
import org.bson.types.ObjectId

object UserFixture {
    val userId = ObjectId()
    val createdUserId = ObjectId()

    fun existingUser() = MongoUser(
        id = userId,
        name = generateString(10),
        email = "${generateString(8)}@gmail.com",
        phoneNumber = generateString(10),
        city = generateString(8),
    )

    fun unexistingUser() = MongoUser(
        id = null,
        name = generateString(10),
        email = "${generateString(8)}@gmail.com",
        phoneNumber = generateString(10),
        city = generateString(8),
    )

    fun responseUser(mongoUser: MongoUser) = UserResponse(
        id = mongoUser.id.toString(),
        name = mongoUser.name ?: generateString(10),
        email = mongoUser.email ?: "${generateString(8)}@gmail.com",
        phoneNumber = mongoUser.phoneNumber ?: generateString(10),
        city = mongoUser.city ?: generateString(8),
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

    fun createdUser(mongoUser: MongoUser) = mongoUser.copy(id = createdUserId)

    fun updateUserRequest() = UpdateUserRequest(
        name = generateString(10),
        phoneNumber = generateString(10),
        city = generateString(8)
    )

    fun updateUserEntity(request: UpdateUserRequest) = MongoUser(
        id = null,
        name = request.name,
        email = null,
        phoneNumber = request.phoneNumber,
        city = request.city,
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

    private fun generateString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charPool[Random.nextInt(charPool.size)] }
            .joinToString("")
    }
}
