package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import org.springframework.stereotype.Repository

@Repository
internal interface CarRepository {

    fun findById(id: String): MongoCar?

    fun findAll(page: Int, size: Int): List<MongoCar>

    fun create(mongoCar: MongoCar): MongoCar

    fun deleteById(id: String)

    fun patch(id: String, carPatch: MongoCarPatch): MongoCar?

    fun findByPlate(plate: String): MongoCar?

    fun findAllByBrand(brand: String): List<MongoCar>

    fun findAllByBrandAndModel(brand: String, model: String): List<MongoCar>
}
