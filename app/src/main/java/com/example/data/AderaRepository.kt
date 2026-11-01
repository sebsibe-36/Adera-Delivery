package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CartItem
import com.example.model.FoodItem
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AderaRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("adera_delivery_prefs", Context.MODE_PRIVATE)

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId: StateFlow<String?> = _activeOrderId.asStateFlow()

    private val _selectedLocation = MutableStateFlow(SampleData.deliveryLocations[0])
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(setOf("food_doro_wat", "food_shekla_tibs"))
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        val savedLocation = prefs.getString("saved_location", null)
        if (!savedLocation.isNullOrEmpty()) {
            _selectedLocation.value = savedLocation
        }

        // Load saved orders or seed initial orders if none exist
        val ordersJsonStr = prefs.getString("saved_orders", null)
        if (!ordersJsonStr.isNullOrEmpty()) {
            try {
                val jsonArr = JSONArray(ordersJsonStr)
                val loadedOrders = mutableListOf<Order>()
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    val id = obj.getString("id")
                    val orderNum = obj.getString("orderNumber")
                    val subtotal = obj.getDouble("subtotal")
                    val deliveryFee = obj.getDouble("deliveryFee")
                    val total = obj.getDouble("total")
                    val statusName = obj.optString("status", OrderStatus.RECEIVED.name)
                    val status = try {
                        OrderStatus.valueOf(statusName)
                    } catch (e: Exception) {
                        OrderStatus.RECEIVED
                    }
                    val customerName = obj.optString("customerName", "Customer")
                    val customerPhone = obj.optString("customerPhone", "+251 91 123 4567")
                    val address = obj.optString("deliveryAddress", "Bole, Addis Ababa")
                    val paymentMethod = obj.optString("paymentMethod", "Cash on Delivery")
                    val notes = obj.optString("deliveryNotes", "")

                    // parse items
                    val itemsArr = obj.optJSONArray("items") ?: JSONArray()
                    val cartList = mutableListOf<CartItem>()
                    for (j in 0 until itemsArr.length()) {
                        val itemObj = itemsArr.getJSONObject(j)
                        val foodId = itemObj.getString("foodId")
                        val qty = itemObj.optInt("quantity", 1)
                        val spice = itemObj.optString("spiceLevel", "Medium")
                        val food = SampleData.foodItems.find { it.id == foodId }
                            ?: SampleData.foodItems.first()
                        cartList.add(CartItem(foodItem = food, quantity = qty, spiceLevel = spice))
                    }

                    loadedOrders.add(
                        Order(
                            id = id,
                            orderNumber = orderNum,
                            items = cartList,
                            subtotal = subtotal,
                            deliveryFee = deliveryFee,
                            total = total,
                            status = status,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            deliveryAddress = address,
                            deliveryNotes = notes,
                            paymentMethod = paymentMethod
                        )
                    )
                }
                _orders.value = loadedOrders
            } catch (e: Exception) {
                _orders.value = SampleData.sampleInitialOrders
            }
        } else {
            _orders.value = SampleData.sampleInitialOrders
            saveOrders(_orders.value)
        }

        // Set active order to the first non-delivered order, or the latest
        val ongoing = _orders.value.find { it.status != OrderStatus.DELIVERED }
        _activeOrderId.value = ongoing?.id ?: _orders.value.firstOrNull()?.id

        // Load saved cart
        val cartJsonStr = prefs.getString("saved_cart", null)
        if (!cartJsonStr.isNullOrEmpty()) {
            try {
                val jsonArr = JSONArray(cartJsonStr)
                val loadedCart = mutableListOf<CartItem>()
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    val foodId = obj.getString("foodId")
                    val qty = obj.getInt("quantity")
                    val spice = obj.optString("spiceLevel", "Medium")
                    val notes = obj.optString("specialInstructions", "")
                    val food = SampleData.foodItems.find { it.id == foodId }
                    if (food != null) {
                        loadedCart.add(CartItem(foodItem = food, quantity = qty, spiceLevel = spice, specialInstructions = notes))
                    }
                }
                _cartItems.value = loadedCart
            } catch (e: Exception) {
                _cartItems.value = emptyList()
            }
        }
    }

    fun setLocation(location: String) {
        _selectedLocation.value = location
        prefs.edit().putString("saved_location", location).apply()
    }

    fun toggleFavorite(foodId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(foodId)) {
            current.remove(foodId)
        } else {
            current.add(foodId)
        }
        _favorites.value = current
    }

    fun addToCart(foodItem: FoodItem, quantity: Int = 1, spiceLevel: String = "Medium", instructions: String = "") {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.foodItem.id == foodItem.id && it.spiceLevel == spiceLevel }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(foodItem = foodItem, quantity = quantity, spiceLevel = spiceLevel, specialInstructions = instructions))
        }
        _cartItems.value = current
        saveCart(current)
    }

    fun updateCartQuantity(foodItemId: String, spiceLevel: String, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.foodItem.id == foodItemId && it.spiceLevel == spiceLevel }
        if (index >= 0) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = newQuantity)
            }
            _cartItems.value = current
            saveCart(current)
        }
    }

    fun removeFromCart(foodItemId: String, spiceLevel: String) {
        val current = _cartItems.value.filterNot { it.foodItem.id == foodItemId && it.spiceLevel == spiceLevel }
        _cartItems.value = current
        saveCart(current)
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        prefs.edit().remove("saved_cart").apply()
    }

    private fun saveCart(items: List<CartItem>) {
        try {
            val arr = JSONArray()
            items.forEach { item ->
                val obj = JSONObject().apply {
                    put("foodId", item.foodItem.id)
                    put("quantity", item.quantity)
                    put("spiceLevel", item.spiceLevel)
                    put("specialInstructions", item.specialInstructions)
                }
                arr.put(obj)
            }
            prefs.edit().putString("saved_cart", arr.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveOrders(ordersList: List<Order>) {
        try {
            val arr = JSONArray()
            ordersList.forEach { order ->
                val obj = JSONObject().apply {
                    put("id", order.id)
                    put("orderNumber", order.orderNumber)
                    put("subtotal", order.subtotal)
                    put("deliveryFee", order.deliveryFee)
                    put("total", order.total)
                    put("status", order.status.name)
                    put("customerName", order.customerName)
                    put("customerPhone", order.customerPhone)
                    put("deliveryAddress", order.deliveryAddress)
                    put("deliveryNotes", order.deliveryNotes)
                    put("paymentMethod", order.paymentMethod)

                    val itemsArr = JSONArray()
                    order.items.forEach { ci ->
                        val itemObj = JSONObject().apply {
                            put("foodId", ci.foodItem.id)
                            put("quantity", ci.quantity)
                            put("spiceLevel", ci.spiceLevel)
                        }
                        itemsArr.put(itemObj)
                    }
                    put("items", itemsArr)
                }
                arr.put(obj)
            }
            prefs.edit().putString("saved_orders", arr.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        address: String,
        notes: String,
        paymentMethod: String
    ): Order {
        val items = _cartItems.value
        val subtotal = items.sumOf { it.totalPrice }
        val deliveryFee = 50.0
        val total = subtotal + deliveryFee
        val randomNum = (1000..9999).random()
        val orderNumber = "AD-$randomNum"
        val orderId = "ord_${UUID.randomUUID().toString().take(8)}"

        val newOrder = Order(
            id = orderId,
            orderNumber = orderNumber,
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            total = total,
            status = OrderStatus.RECEIVED,
            customerName = customerName.ifBlank { "Adera Customer" },
            customerPhone = customerPhone.ifBlank { "+251 91 234 5678" },
            deliveryAddress = address.ifBlank { _selectedLocation.value },
            deliveryNotes = notes,
            paymentMethod = paymentMethod,
            estimatedArrival = "25-35 mins",
            driverName = listOf("Abebe Kebede", "Dawit Girma", "Tariku Bekele", "Henok Alemu").random(),
            driverPhone = "+251 91 ${ (100..999).random() } ${ (1000..9999).random() }"
        )

        val updated = listOf(newOrder) + _orders.value
        _orders.value = updated
        _activeOrderId.value = orderId
        saveOrders(updated)
        clearCart()
        return newOrder
    }

    fun setActiveOrder(orderId: String) {
        _activeOrderId.value = orderId
    }

    fun advanceOrderStatus(orderId: String) {
        val list = _orders.value.map { order ->
            if (order.id == orderId) {
                val nextStatus = when (order.status) {
                    OrderStatus.RECEIVED -> OrderStatus.PREPARING
                    OrderStatus.PREPARING -> OrderStatus.READY_FOR_DELIVERY
                    OrderStatus.READY_FOR_DELIVERY -> OrderStatus.DELIVERED
                    OrderStatus.DELIVERED -> OrderStatus.RECEIVED
                }
                order.copy(status = nextStatus)
            } else order
        }
        _orders.value = list
        saveOrders(list)
    }

    fun reorder(order: Order) {
        order.items.forEach { ci ->
            addToCart(ci.foodItem, ci.quantity, ci.spiceLevel, ci.specialInstructions)
        }
    }
}
