package fixtures

import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.User
import kotlin.random.Random
import org.bson.types.ObjectId

object UserFixture {
    val userId = ObjectId().toHexString()
    val createdUserId = ObjectId().toHexString()

    fun existingUser() = User(
        id = userId,
        name = generateString(10),
        email = "${generateString(8)}@gmail.com",
        phoneNumber = generateString(10),
        city = generateString(8),
    )

    fun responseUser(user: User) = UserResponse(
        id = user.id ?: userId,
        name = user.name ?: generateString(10),
        email = user.email ?: "${generateString(8)}@gmail.com",
        phoneNumber = user.phoneNumber ?: generateString(10),
        city = user.city ?: generateString(8),
    )

    fun createUserRequest() = CreateUserRequest(
        name = generateString(10),
        email = "${generateString(8)}@gmail.com",
        phoneNumber = generateString(10),
        city = generateString(8),
    )

    fun createUserEntity(request: CreateUserRequest) = User(
        id = null,
        name = request.name,
        email = request.email,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun createdUser(user: User) = user.copy(id = createdUserId)

    fun createdUserResponse(user: User) = UserResponse(
        id = user.id ?: createdUserId,
        name = user.name ?: generateString(10),
        email = user.email ?: "${generateString(8)}@gmail.com",
        phoneNumber = user.phoneNumber ?: generateString(10),
        city = user.city ?: generateString(8),
    )

    fun updateUserRequest() = UpdateUserRequest(
        name = generateString(10),
        phoneNumber = generateString(10),
        city = generateString(8)
    )

    fun updateUserEntity(request: UpdateUserRequest) = User(
        id = null,
        name = request.name,
        email = null,
        phoneNumber = request.phoneNumber,
        city = request.city,
    )

    fun updatedUser(user: User, request: UpdateUserRequest) =
        user.copy(name = request.name, phoneNumber = request.phoneNumber, city = request.city)

    private fun generateString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charPool[Random.nextInt(charPool.size)] }
            .joinToString("")
    }
}
