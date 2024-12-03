package com.makarytskyi.rentcar.user.infrastructure.mongo

import com.makarytskyi.rentcar.user.application.port.output.UserOutputPort
import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.infrastructure.mongo.entity.MongoUser
import com.makarytskyi.rentcar.user.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.user.infrastructure.mongo.mapper.toMongo
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
internal class MongoUserRepository(private val template: ReactiveMongoTemplate) : UserOutputPort {

    override fun create(user: DomainUser): Mono<DomainUser> {
        return template.insert(user.toMongo()).map { it.toDomain() }
    }

    override fun findById(id: String): Mono<DomainUser> {
        return template.findById<MongoUser>(id).map { it.toDomain() }
    }

    override fun findAll(page: Int, size: Int): Flux<DomainUser> {
        val query = Query().with(PageRequest.of(page, size))
        return template.find(query, MongoUser::class.java).map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoUser::class.java).thenReturn(Unit)
    }

    override fun patch(id: String, patch: DomainUser): Mono<DomainUser> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()
            .set(MongoUser::name.name, patch.name)
            .set(MongoUser::phoneNumber.name, patch.phoneNumber)
            .set(MongoUser::city.name, patch.city)

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoUser::class.java).map { it.toDomain() }
    }

    override fun findByPhoneNumber(phoneNumber: String): Mono<DomainUser> {
        val query = Query(Criteria.where(MongoUser::phoneNumber.name).isEqualTo(phoneNumber))
        return template.findOne(query, MongoUser::class.java).map { it.toDomain() }
    }

    override fun findByEmail(email: String): Mono<DomainUser> {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return template.findOne(query, MongoUser::class.java).map { it.toDomain() }
    }
}
