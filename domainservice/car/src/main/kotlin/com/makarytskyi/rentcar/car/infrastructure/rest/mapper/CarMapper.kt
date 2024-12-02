package com.makarytskyi.rentcar.car.infrastructure.rest.mapper

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CreateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.UpdateCarRequest
import java.math.BigDecimal


fun CreateCarRequest.toDomain(): DomainCar = DomainCar(
    id = null,
    brand = this.brand,
    model = this.model,
    price = this.price,
    year = this.year,
    plate = this.plate,
    color = this.color ?: DomainCar.CarColor.UNSPECIFIED,
)

fun UpdateCarRequest.toPatch(): DomainCarPatch = DomainCarPatch(
    price = this.price,
    color = this.color,
)

fun DomainCar.toResponse(): CarResponse = CarResponse(
    requireNotNull(this.id) { "Car id is null" },
    this.brand,
    this.model,
    this.price,
    this.year,
    this.plate,
    this.color,
)
