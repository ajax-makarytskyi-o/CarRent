package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.repository.CarRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
internal class CarRepositoryImpl(private val template: MongoTemplate) : CarRepository {

    override fun findById(id: String): MongoCar? {
        return template.findById<MongoCar>(id)
    }

    override fun create(mongoCar: MongoCar): MongoCar {
        return template.insert(mongoCar)
    }

    override fun deleteById(id: String) {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        template.remove(query, MongoCar::class.java)
    }

    override fun findAll(): List<MongoCar> {
        return template.findAll<MongoCar>()
    }

    override fun findAllByBrand(brand: String): List<MongoCar> {
        val query = Query(Criteria.where(MongoCar::brand.name).isEqualTo(brand))
        return template.find(query, MongoCar::class.java)
    }

    override fun findAllByBrandAndModel(brand: String, model: String): List<MongoCar> {
        val query = Query(
            Criteria.where(MongoCar::brand.name).isEqualTo(brand).and(MongoCar::model.name).isEqualTo(model)
        )
        return template.find(query, MongoCar::class.java)
    }

    override fun update(id: String, mongoCar: MongoCar): MongoCar? {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        mongoCar.color?.let { update.set(MongoCar::color.name, it) }
        mongoCar.price?.let { update.set(MongoCar::price.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoCar::class.java)
    }

    override fun findByPlate(plate: String): MongoCar? {
        val query = Query(Criteria.where(MongoCar::plate.name).isEqualTo(plate))
        return template.findOne(query, MongoCar::class.java)
    }
}
