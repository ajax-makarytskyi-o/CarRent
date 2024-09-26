package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoCar
import org.springframework.stereotype.Repository

@Repository
internal interface CarRepository {

    fun findById(id: String): MongoCar?

    fun findAll(): List<MongoCar>

    fun create(mongoCar: MongoCar): MongoCar

    fun deleteById(id: String)

    fun update(id: String, mongoCar: MongoCar): MongoCar?

    fun findByPlate(plate: String): MongoCar?

    fun findAllByBrand(brand: String): List<MongoCar>

    fun findAllByBrandAndModel(brand: String, model: String): List<MongoCar>
}
