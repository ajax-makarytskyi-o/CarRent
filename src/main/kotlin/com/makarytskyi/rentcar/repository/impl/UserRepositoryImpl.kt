package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.repository.UserRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
internal class UserRepositoryImpl(private val template: MongoTemplate) : UserRepository {

    override fun create(mongoUser: MongoUser): MongoUser = template.insert(mongoUser)

    override fun findById(id: String) = template.findById<MongoUser>(id)

    override fun findAll(): List<MongoUser> = template.findAll<MongoUser>()

    override fun deleteById(id: String) {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        template.remove(query, MongoUser::class.java)
    }

    override fun update(id: String, mongoUser: MongoUser): MongoUser? {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        val update = Update()

        mongoUser.name?.let { update.set("name", it) }
        mongoUser.phoneNumber?.let { update.set("phoneNumber", it) }
        mongoUser.city?.let { update.set("city", it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoUser::class.java)

    }

    override fun findByPhoneNumber(phoneNumber: String): MongoUser? {
        val query = Query(Criteria.where("phoneNumber").isEqualTo(phoneNumber))
        return template.findOne(query, MongoUser::class.java)
    }

    override fun findByEmail(email: String): MongoUser? {
        val query = Query(Criteria.where("email").isEqualTo(email))
        return template.findOne(query, MongoUser::class.java)
    }
}
