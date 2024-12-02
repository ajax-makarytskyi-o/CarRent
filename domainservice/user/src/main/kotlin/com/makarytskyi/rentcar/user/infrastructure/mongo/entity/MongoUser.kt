package com.makarytskyi.rentcar.user.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: ObjectId? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val city: String? = null,
) {
    companion object {
        const val COLLECTION_NAME = "users"
    }
}
