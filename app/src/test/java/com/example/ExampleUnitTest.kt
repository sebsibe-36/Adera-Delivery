package com.example

import com.example.data.SampleData
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.OrderStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sampleData_containsRequiredEthiopianDishes() {
        assertTrue("At least 5 restaurants", SampleData.restaurants.size >= 5)
        assertTrue("At least 15 food items", SampleData.foodItems.size >= 15)
        assertTrue("At least 5 categories", SampleData.categories.size >= 5)

        val doroWat = SampleData.foodItems.find { it.name.contains("Doro Wat") }
        assertNotNull("Doro Wat must exist", doroWat)

        val tibs = SampleData.foodItems.find { it.name.contains("Tibs") }
        assertNotNull("Tibs must exist", tibs)

        val beyaynet = SampleData.foodItems.find { it.name.contains("Beyaynet") }
        assertNotNull("Beyaynet must exist", beyaynet)

        val kitfo = SampleData.foodItems.find { it.name.contains("Kitfo") }
        assertNotNull("Kitfo must exist", kitfo)
    }

    @Test
    fun cartItem_priceCalculation() {
        val food = SampleData.foodItems.first()
        val cartItem = CartItem(foodItem = food, quantity = 3, spiceLevel = "Medium")
        assertEquals(food.price * 3, cartItem.totalPrice, 0.001)
    }

    @Test
    fun orderStatus_flowIntegrity() {
        assertEquals(0, OrderStatus.RECEIVED.stepIndex)
        assertEquals(1, OrderStatus.PREPARING.stepIndex)
        assertEquals(2, OrderStatus.READY_FOR_DELIVERY.stepIndex)
        assertEquals(3, OrderStatus.DELIVERED.stepIndex)
    }
}
