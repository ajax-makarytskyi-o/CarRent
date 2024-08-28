package user

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.User

object UserFixture {
    const val userId: String = "1241242"
    const val createdUserId: String = "83465294"

    val existingUser = User(
        id = userId,
        name = "john",
        email = "john@gmail.com",
        phoneNumber = "1234567890",
        city = "Kyiv",
    )

    val responseUser = UserResponse(
        id = userId,
        name = "john",
        email = "john@gmail.com",
        phoneNumber = "1234567890",
        city = "Kyiv",
    )

    val createUserRequest = CreateUserRequest(
        name = "steve",
        email = "stv@gmail.com",
        phoneNumber = "1223334444",
        city = "Lviv",
    )

    val createUserEntity = User(
        id = null,
        name = "steve",
        email = "stv@gmail.com",
        phoneNumber = "1223334444",
        city = "Lviv",
    )

    val createdUser = User(
        id = createdUserId,
        name = "steve",
        email = "stv@gmail.com",
        phoneNumber = "1223334444",
        city = "Lviv",
    )

    val createdUserResponse = UserResponse(
        id = createdUserId,
        name = "steve",
        email = "stv@gmail.com",
        phoneNumber = "1223334444",
        city = "Lviv",
    )

    val updateUserRequest = UpdateUserRequest(
        name = "jake",
        phoneNumber = "0987654321",
        city = "Zaporizhzhia"
    )

    val updateUserEntity = User(
        id = null,
        name = "jake",
        email = null,
        phoneNumber = "0987654321",
        city = "Zaporizhzhia",
    )

    val updatedUser = User(
        id = userId,
        name = "jake",
        email = "stv@gmail.com",
        phoneNumber = "0987654321",
        city = "Zaporizhzhia",
    )
}
