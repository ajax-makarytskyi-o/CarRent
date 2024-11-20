package com.makarytskyi.gateway.fixtures

import com.makarytskyi.commonmodels.user.User
import com.makarytskyi.gateway.fixtures.Utils.generateString
import org.bson.types.ObjectId

object UserProtoFixture {
    fun randomUser(): User = User.newBuilder()
        .apply {
            setId(ObjectId().toString())
            setName(generateString(10))
            setEmail("${generateString(10)}@email.com")
            setCity(generateString(8))
            setPhoneNumber(generateString(10))
        }
        .build()
}
