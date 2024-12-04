package com.makarytskyi.rentcar.car.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.create.CreateCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar

fun CreateCar.toMongo(): MongoCar = MongoCar(
    brand = this.brand,
    model = this.model,
    price = this.price,
    year = this.year,
    plate = this.plate,
    color = this.color.toMongo(),
)

fun DomainCar.CarColor.toMongo(): MongoCar.CarColor =
    when (this) {
        DomainCar.CarColor.RED -> MongoCar.CarColor.RED
        DomainCar.CarColor.GREEN -> MongoCar.CarColor.GREEN
        DomainCar.CarColor.BLUE -> MongoCar.CarColor.BLUE
        DomainCar.CarColor.BLACK -> MongoCar.CarColor.BLACK
        DomainCar.CarColor.WHITE -> MongoCar.CarColor.WHITE
        DomainCar.CarColor.GREY -> MongoCar.CarColor.GREY
        DomainCar.CarColor.YELLOW -> MongoCar.CarColor.YELLOW
        DomainCar.CarColor.UNSPECIFIED -> MongoCar.CarColor.UNSPECIFIED
    }

fun MongoCar.toDomain(): DomainCar = DomainCar(
    id = this.id.toString(),
    brand = requireNotNull(this.brand) { "Brand of car is null" },
    model = requireNotNull(this.model) { "Model of car is null" },
    price = requireNotNull(this.price) { "Price of car is null" },
    year = this.year,
    plate = requireNotNull(this.plate) { "Plate of car is null" },
    color = this.color?.toDomain() ?: DomainCar.CarColor.UNSPECIFIED,
)

fun MongoCar.CarColor.toDomain(): DomainCar.CarColor =
    when (this) {
        MongoCar.CarColor.RED -> DomainCar.CarColor.RED
        MongoCar.CarColor.GREEN -> DomainCar.CarColor.GREEN
        MongoCar.CarColor.BLUE -> DomainCar.CarColor.BLUE
        MongoCar.CarColor.BLACK -> DomainCar.CarColor.BLACK
        MongoCar.CarColor.WHITE -> DomainCar.CarColor.WHITE
        MongoCar.CarColor.GREY -> DomainCar.CarColor.GREY
        MongoCar.CarColor.YELLOW -> DomainCar.CarColor.YELLOW
        MongoCar.CarColor.UNSPECIFIED -> DomainCar.CarColor.UNSPECIFIED
    }
