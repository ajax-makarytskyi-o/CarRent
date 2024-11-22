package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import com.makarytskyi.rentcar.repository.CarRepository
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal class MongoCarRepository(private val template: ReactiveMongoTemplate) : CarRepository {

    override fun findById(id: String): Mono<MongoCar> {
        return template.findById<MongoCar>(id)
    }

    override fun create(mongoCar: MongoCar): Mono<MongoCar> {
        return template.insert(mongoCar)
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoCar::class.java).thenReturn(Unit)
    }

    override fun findAll(page: Int, size: Int): Flux<MongoCar> {
        val query = Query().with(PageRequest.of(page, size))
        return template.find(query, MongoCar::class.java)
    }

    override fun findAllByBrand(brand: String): Flux<MongoCar> {
        val query = Query(Criteria.where(MongoCar::brand.name).isEqualTo(brand))
        return template.find(query, MongoCar::class.java)
    }

    override fun findAllByBrandAndModel(brand: String, model: String): Flux<MongoCar> {
        val query = Query(
            Criteria.where(MongoCar::brand.name).isEqualTo(brand).and(MongoCar::model.name).isEqualTo(model)
        )
        return template.find(query, MongoCar::class.java)
    }

    override fun patch(id: String, carPatch: MongoCarPatch): Mono<MongoCar> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        carPatch.color?.let { update.set(MongoCar::color.name, it) }
        carPatch.price?.let { update.set(MongoCar::price.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoCar::class.java)
    }

    override fun findByPlate(plate: String): Mono<MongoCar> {
        val query = Query(Criteria.where(MongoCar::plate.name).isEqualTo(plate))
        return template.findOne(query, MongoCar::class.java)
    }
}
