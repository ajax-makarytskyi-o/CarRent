package com.makarytskyi.rentcar.order.domain

import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.order.domain.patch.PatchOrder
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId

class OrderDomainMapper {
    @Test
    fun `patch mapper should return order with updated fields`() {
        // GIVEN
        val order = randomOrder(ObjectId().toString(), ObjectId().toString())

        val patch = PatchOrder(
            from = monthAfter,
            to = monthAndDayAfter,
        )

        val expected = order.copy(from = patch.from!!, to = patch.to!!)

        // WHEN
        val result = order.fromPatch(patch)

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun `patch mapper should return old order if patch is empty`() {
        // GIVEN
        val order = randomOrder(ObjectId().toString(), ObjectId().toString())

        val patch = PatchOrder(
            from = null,
            to = null,
        )

        // WHEN
        val result = order.fromPatch(patch)

        // THEN
        assertEquals(order, result)
    }
}
