package com.makarytskyi.rentcar.model

import java.math.BigDecimal
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Car")
@Document(collection = MongoCar.COLLECTION_NAME)
data class MongoCar(
    @Id
    val id: ObjectId? = null,
    val brand: String? = null,
    val model: String? = null,
    val price: BigDecimal? = null,
    val year: Int? = null,
    val plate: String? = null,
    var color: CarColor? = null,
) {
    companion object {
        const val COLLECTION_NAME = "cars"
    }

    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW
    }
}
