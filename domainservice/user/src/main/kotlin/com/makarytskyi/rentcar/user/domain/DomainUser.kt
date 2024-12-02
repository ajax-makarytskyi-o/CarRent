package com.makarytskyi.rentcar.user.domain

import com.makarytskyi.rentcar.user.domain.patch.DomainUserPatch

data class DomainUser(
    val id: String?,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val city: String?,
) {
    fun fromPatch(patch: DomainUserPatch): DomainUser = this.copy(
        name = patch.name ?: this.name,
        phoneNumber = patch.phoneNumber ?: this.phoneNumber,
        city = patch.city ?: this.city,
    )
}
