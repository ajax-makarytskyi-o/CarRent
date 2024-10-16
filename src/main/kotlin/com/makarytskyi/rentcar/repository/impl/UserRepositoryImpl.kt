package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoUserPatch
import com.makarytskyi.rentcar.repository.UserRepository
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
internal class UserRepositoryImpl(private val template: ReactiveMongoTemplate) : UserRepository {

    override fun create(mongoUser: MongoUser): Mono<MongoUser> {
        println("user create")

        return template.insert(mongoUser)
    }

    override fun findById(id: String): Mono<MongoUser> {
        return template.findById<MongoUser>(id)
    }

    override fun findAll(page: Int, size: Int): Flux<MongoUser> {
        val query = Query().with(PageRequest.of(page, size))
        return template.find(query, MongoUser::class.java)
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoUser::class.java).thenReturn(Unit)
    }

    override fun patch(id: String, userPatch: MongoUserPatch): Mono<MongoUser> {
        println("user patch")
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        userPatch.name?.let { update.set(MongoUser::name.name, it) }
        userPatch.phoneNumber?.let { update.set(MongoUser::phoneNumber.name, it) }
        userPatch.city?.let { update.set(MongoUser::city.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoUser::class.java)
    }

    override fun findByPhoneNumber(phoneNumber: String): Mono<MongoUser> {
        val query = Query(Criteria.where(MongoUser::phoneNumber.name).isEqualTo(phoneNumber))
        return template.findOne(query, MongoUser::class.java)
    }

    override fun findByEmail(email: String): Mono<MongoUser> {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return template.findOne(query, MongoUser::class.java)
    }
}
