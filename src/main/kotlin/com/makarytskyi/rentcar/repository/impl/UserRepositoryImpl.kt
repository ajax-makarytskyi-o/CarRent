package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
internal class UserRepositoryImpl(private val template: MongoTemplate) : UserRepository {

    override fun create(mongoUser: MongoUser): MongoUser {
        return template.insert(mongoUser)
    }

    override fun findById(id: String): MongoUser? {
        return template.findById<MongoUser>(id)
    }

    override fun findAll(page: Int, size: Int): List<MongoUser> {
        val query = Query().with(PageRequest.of(page, size))
        return template.find(query, MongoUser::class.java)
    }

    override fun deleteById(id: String) {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        template.remove(query, MongoUser::class.java)
    }

    override fun patch(id: String, mongoUser: MongoUser): MongoUser? {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        mongoUser.name?.let { update.set(MongoUser::name.name, it) }
        mongoUser.phoneNumber?.let { update.set(MongoUser::phoneNumber.name, it) }
        mongoUser.city?.let { update.set(MongoUser::city.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoUser::class.java)
    }

    override fun findByPhoneNumber(phoneNumber: String): MongoUser? {
        val query = Query(Criteria.where(MongoUser::phoneNumber.name).isEqualTo(phoneNumber))
        return template.findOne(query, MongoUser::class.java)
    }

    override fun findByEmail(email: String): MongoUser? {
        val query = Query(Criteria.where(MongoUser::email.name).isEqualTo(email))
        return template.findOne(query, MongoUser::class.java)
    }
}
