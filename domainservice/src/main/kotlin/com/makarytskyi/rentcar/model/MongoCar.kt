package com.makarytskyi.rentcar.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.math.BigDecimal
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Car")
@Document(collection = MongoCar.COLLECTION_NAME)
data class MongoCar(
    @Id
    @JsonSerialize(using = ToStringSerializer::class)
    val id: ObjectId? = null,
    val brand: String? = null,
    val model: String? = null,
    val price: BigDecimal? = null,
    val year: Int? = null,
    val plate: String? = null,
    var color: CarColor? = null,
) {
    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW, UNSPECIFIED
    }

    companion object {
        const val COLLECTION_NAME = "cars"
    }
}
