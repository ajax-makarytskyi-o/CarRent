package com.makarytskyi.rentcar.car.infrastructure.rest.mapper

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CreateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.UpdateCarRequest

fun CreateCarRequest.toDomain(): DomainCar = DomainCar(
    id = null,
    brand = this.brand,
    model = this.model,
    price = this.price,
    year = this.year,
    plate = this.plate,
    color = this.color.toDomain(),
)

fun UpdateCarRequest.toPatch(): DomainCarPatch = DomainCarPatch(
    price = this.price,
    color = this.color?.toDomain(),
)

fun DomainCar.toResponse(): CarResponse = CarResponse(
    requireNotNull(this.id) { "Car id is null" },
    this.brand,
    this.model,
    this.price,
    this.year,
    this.plate,
    this.color.toResponse(),
)

fun CreateCarRequest.CarColor.toDomain(): DomainCar.CarColor =
    when (this) {
        CreateCarRequest.CarColor.RED -> DomainCar.CarColor.RED
        CreateCarRequest.CarColor.GREEN -> DomainCar.CarColor.GREEN
        CreateCarRequest.CarColor.BLUE -> DomainCar.CarColor.BLUE
        CreateCarRequest.CarColor.BLACK -> DomainCar.CarColor.BLACK
        CreateCarRequest.CarColor.WHITE -> DomainCar.CarColor.WHITE
        CreateCarRequest.CarColor.GREY -> DomainCar.CarColor.GREY
        CreateCarRequest.CarColor.YELLOW -> DomainCar.CarColor.YELLOW
        CreateCarRequest.CarColor.UNSPECIFIED -> DomainCar.CarColor.UNSPECIFIED
    }

fun UpdateCarRequest.CarColor.toDomain(): DomainCar.CarColor =
    when (this) {
        UpdateCarRequest.CarColor.RED -> DomainCar.CarColor.RED
        UpdateCarRequest.CarColor.GREEN -> DomainCar.CarColor.GREEN
        UpdateCarRequest.CarColor.BLUE -> DomainCar.CarColor.BLUE
        UpdateCarRequest.CarColor.BLACK -> DomainCar.CarColor.BLACK
        UpdateCarRequest.CarColor.WHITE -> DomainCar.CarColor.WHITE
        UpdateCarRequest.CarColor.GREY -> DomainCar.CarColor.GREY
        UpdateCarRequest.CarColor.YELLOW -> DomainCar.CarColor.YELLOW
        UpdateCarRequest.CarColor.UNSPECIFIED -> DomainCar.CarColor.UNSPECIFIED
    }

fun DomainCar.CarColor.toResponse(): CarResponse.CarColor =
    when (this) {
        DomainCar.CarColor.RED -> CarResponse.CarColor.RED
        DomainCar.CarColor.GREEN -> CarResponse.CarColor.GREEN
        DomainCar.CarColor.BLUE -> CarResponse.CarColor.BLUE
        DomainCar.CarColor.BLACK -> CarResponse.CarColor.BLACK
        DomainCar.CarColor.WHITE -> CarResponse.CarColor.WHITE
        DomainCar.CarColor.GREY -> CarResponse.CarColor.GREY
        DomainCar.CarColor.YELLOW -> CarResponse.CarColor.YELLOW
        DomainCar.CarColor.UNSPECIFIED -> CarResponse.CarColor.UNSPECIFIED
    }
