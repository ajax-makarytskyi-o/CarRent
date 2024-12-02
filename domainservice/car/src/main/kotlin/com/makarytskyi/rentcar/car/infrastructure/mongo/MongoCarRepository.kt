package com.makarytskyi.rentcar.car.infrastructure.mongo

import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toMongo
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
class MongoCarRepository(private val template: ReactiveMongoTemplate) : CarOutputPort {

    override fun findById(id: String): Mono<DomainCar> {
        return template.findById<MongoCar>(id).map { it.toDomain() }
    }

    override fun create(car: DomainCar): Mono<DomainCar> {
        return template.insert(car.toMongo()).map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoCar::class.java).thenReturn(Unit)
    }

    override fun findAll(page: Int, size: Int): Flux<DomainCar> {
        val query = Query().with(PageRequest.of(page, size))
        return template.find(query, MongoCar::class.java).map { it.toDomain() }
    }

    override fun findAllByBrand(brand: String): Flux<DomainCar> {
        val query = Query(Criteria.where(MongoCar::brand.name).isEqualTo(brand))
        return template.find(query, MongoCar::class.java).map { it.toDomain() }
    }

    override fun findAllByBrandAndModel(brand: String, model: String): Flux<DomainCar> {
        val query = Query(
            Criteria.where(MongoCar::brand.name).isEqualTo(brand).and(MongoCar::model.name).isEqualTo(model)
        )
        return template.find(query, MongoCar::class.java).map { it.toDomain() }
    }

    override fun patch(id: String, carPatch: DomainCar): Mono<DomainCar> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        update.set(MongoCar::color.name, carPatch.color)
        update.set(MongoCar::price.name, carPatch.price)

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoCar::class.java).map { it.toDomain() }
    }

    override fun findByPlate(plate: String): Mono<DomainCar> {
        val query = Query(Criteria.where(MongoCar::plate.name).isEqualTo(plate))
        return template.findOne(query, MongoCar::class.java).map { it.toDomain() }
    }
}
