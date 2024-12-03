package com.makarytskyi.rentcar.repairing.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.MongoRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.projection.AggregatedMongoRepairing
import org.bson.types.ObjectId

fun AggregatedMongoRepairing.toDomain(): AggregatedDomainRepairing = AggregatedDomainRepairing(
    id = this.id.toString(),
    car = this.car?.toDomain() ?: throw IllegalArgumentException("Car in repairing is null"),
    date = this.date ?: throw IllegalArgumentException("Date of repairing is null"),
    price = this.price ?: throw IllegalArgumentException("Repairing price is null"),
    status = this.status?.toDomain() ?: DomainRepairing.RepairingStatus.PENDING,
)

fun MongoRepairing.toDomain(): DomainRepairing = DomainRepairing(
    id = this.id.toString(),
    carId = this.carId.toString(),
    date = this.date ?: throw IllegalArgumentException("Date of repairing is null"),
    price = this.price ?: throw IllegalArgumentException("Repairing price is null"),
    status = this.status?.toDomain() ?: DomainRepairing.RepairingStatus.PENDING,
)

fun MongoRepairing.RepairingStatus.toDomain(): DomainRepairing.RepairingStatus =
    when(this) {
        MongoRepairing.RepairingStatus.PENDING -> DomainRepairing.RepairingStatus.PENDING
        MongoRepairing.RepairingStatus.IN_PROGRESS -> DomainRepairing.RepairingStatus.IN_PROGRESS
        MongoRepairing.RepairingStatus.COMPLETED -> DomainRepairing.RepairingStatus.COMPLETED
    }

fun DomainRepairing.toMongo(): MongoRepairing = MongoRepairing(
    id = this.id?.let { ObjectId(it) },
    carId = ObjectId(this.carId),
    date = this.date,
    price = this.price,
    status = this.status.toMongo(),
)

fun DomainRepairing.RepairingStatus.toMongo(): MongoRepairing.RepairingStatus =
    when(this) {
        DomainRepairing.RepairingStatus.PENDING -> MongoRepairing.RepairingStatus.PENDING
        DomainRepairing.RepairingStatus.IN_PROGRESS -> MongoRepairing.RepairingStatus.IN_PROGRESS
        DomainRepairing.RepairingStatus.COMPLETED -> MongoRepairing.RepairingStatus.COMPLETED
    }
