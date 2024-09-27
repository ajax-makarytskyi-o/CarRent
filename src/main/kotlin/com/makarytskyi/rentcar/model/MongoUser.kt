package com.makarytskyi.rentcar.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: ObjectId? = null,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?,
) {
    companion object {
        const val COLLECTION_NAME = "users"
    }
}
