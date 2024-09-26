package com.makarytskyi.rentcar.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Car")
@Document(collection = MongoCar.COLLECTION_NAME)
data class MongoCar(
    @Id
    val id: ObjectId? = null,
    val brand: String?,
    val model: String?,
    val price: Int?,
    val year: Int?,
    val plate: String?,
    var color: CarColor?,
) {
    companion object {
        const val COLLECTION_NAME = "cars"
    }

    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW;
    }
}
