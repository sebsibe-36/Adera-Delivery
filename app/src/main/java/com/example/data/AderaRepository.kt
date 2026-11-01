package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.R
import com.example.model.CartItem
import com.example.model.FoodItem
import com.example.model.Order
import com.example.model.OrderStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AderaRepository(
    private val context: Context,
    private val firestoreInstance: FirebaseFirestore? = null
) {
    private val tag = "AderaRepository"
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val prefs: SharedPreferences =
        context.getSharedPreferences("adera_delivery_prefs", Context.MODE_PRIVATE)

    // Resolve named database explicitly from firebase_applet_config.xml
    private val db: FirebaseFirestore by lazy {
        firestoreInstance ?: try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize Firestore with named database ID", e)
            FirebaseFirestore.getInstance()
        }
    }

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId: StateFlow<String?> = _activeOrderId.asStateFlow()

    private val _selectedLocation = MutableStateFlow(SampleData.deliveryLocations[0])
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _favoriteFoodIds = MutableStateFlow<Set<String>>(setOf("food_doro_wat", "food_shekla_tibs"))
    val favoriteFoodIds: StateFlow<Set<String>> = _favoriteFoodIds.asStateFlow()
    val favorites: StateFlow<Set<String>> = _favoriteFoodIds.asStateFlow()

    private val _favoriteRestaurantIds = MutableStateFlow<Set<String>>(setOf("rest_1", "rest_2"))
    val favoriteRestaurantIds: StateFlow<Set<String>> = _favoriteRestaurantIds.asStateFlow()

    private val _isCloudSynced = MutableStateFlow(false)
    val isCloudSynced: StateFlow<Boolean> = _isCloudSynced.asStateFlow()

    private var ordersListener: ListenerRegistration? = null
    private var favoritesListener: ListenerRegistration? = null

    init {
        loadSavedData()
        // Check current authenticated user and attach listeners if present
        val user = Firebase.auth.currentUser
        if (user != null) {
            attachUserListeners(user.uid)
            syncUserProfile(user)
        }
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

        // Load saved favorite food items
        val savedFavFoods = prefs.getStringSet("saved_favorite_foods", null)
        if (savedFavFoods != null) {
            _favoriteFoodIds.value = savedFavFoods
        }

        // Load saved favorite restaurants
        val savedFavRestaurants = prefs.getStringSet("saved_favorite_restaurants", null)
        if (savedFavRestaurants != null) {
            _favoriteRestaurantIds.value = savedFavRestaurants
        }
    }

    fun onUserSignedIn(user: FirebaseUser) {
        attachUserListeners(user.uid)
        syncUserProfile(user)
        syncLocalDataToFirestore(user.uid)
        _isCloudSynced.value = true
    }

    fun onUserSignedOut() {
        ordersListener?.remove()
        ordersListener = null
        favoritesListener?.remove()
        favoritesListener = null
        _isCloudSynced.value = false
    }

    private fun syncUserProfile(user: FirebaseUser) {
        val userRef = db.collection("users").document(user.uid)
        val profileData = mutableMapOf<String, Any>(
            "userId" to user.uid,
            "displayName" to (user.displayName ?: "Adera Customer"),
            "email" to (user.email ?: ""),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        user.photoUrl?.let {
            profileData["photoUrl"] = it.toString()
        }

        // Check if document exists first to maintain createdAt
        userRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                profileData["createdAt"] = FieldValue.serverTimestamp()
                userRef.set(profileData)
                    .addOnFailureListener { e -> Log.e(tag, "Failed to create user profile in Firestore", e) }
            } else {
                userRef.set(profileData, SetOptions.merge())
                    .addOnFailureListener { e -> Log.e(tag, "Failed to update user profile in Firestore", e) }
            }
        }.addOnFailureListener { e ->
            Log.e(tag, "Failed to fetch user document", e)
        }
    }

    private fun attachUserListeners(uid: String) {
        ordersListener?.remove()
        favoritesListener?.remove()

        // 1. Listen to /users/{uid}/orders
        ordersListener = db.collection("users").document(uid).collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "Error listening to orders", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val firestoreOrders = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: doc.id
                            val orderNumber = doc.getString("orderNumber") ?: "AD-0000"
                            val subtotal = doc.getDouble("subtotal") ?: 0.0
                            val deliveryFee = doc.getDouble("deliveryFee") ?: 50.0
                            val total = doc.getDouble("total") ?: (subtotal + deliveryFee)
                            val statusStr = doc.getString("status") ?: OrderStatus.RECEIVED.name
                            val status = try { OrderStatus.valueOf(statusStr) } catch (_: Exception) { OrderStatus.RECEIVED }
                            val customerName = doc.getString("customerName") ?: ""
                            val customerPhone = doc.getString("customerPhone") ?: ""
                            val address = doc.getString("deliveryAddress") ?: ""
                            val notes = doc.getString("deliveryNotes") ?: ""
                            val payment = doc.getString("paymentMethod") ?: "Cash on Delivery"
                            val arrival = doc.getString("estimatedArrival") ?: "25-35 mins"
                            val driver = doc.getString("driverName") ?: "Abebe Kebede"
                            val driverPhone = doc.getString("driverPhone") ?: "+251 91 123 4567"

                            val itemsRaw = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                            val parsedItems = itemsRaw.map { itemMap ->
                                val foodId = itemMap["foodId"] as? String ?: ""
                                val qty = (itemMap["quantity"] as? Number)?.toInt() ?: 1
                                val spice = itemMap["spiceLevel"] as? String ?: "Medium"
                                val food = SampleData.foodItems.find { it.id == foodId }
                                    ?: SampleData.foodItems.first()
                                CartItem(foodItem = food, quantity = qty, spiceLevel = spice)
                            }

                            Order(
                                id = id,
                                orderNumber = orderNumber,
                                items = parsedItems,
                                subtotal = subtotal,
                                deliveryFee = deliveryFee,
                                total = total,
                                status = status,
                                customerName = customerName,
                                customerPhone = customerPhone,
                                deliveryAddress = address,
                                deliveryNotes = notes,
                                paymentMethod = payment,
                                estimatedArrival = arrival,
                                driverName = driver,
                                driverPhone = driverPhone
                            )
                        } catch (e: Exception) {
                            Log.e(tag, "Failed to parse order from document ${doc.id}", e)
                            null
                        }
                    }

                    if (firestoreOrders.isNotEmpty()) {
                        _orders.value = firestoreOrders
                        saveOrders(firestoreOrders)
                    }
                }
            }

        // 2. Listen to /users/{uid}/favorites
        favoritesListener = db.collection("users").document(uid).collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "Error listening to favorites", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val foodFavs = mutableSetOf<String>()
                    val restFavs = mutableSetOf<String>()
                    for (doc in snapshot.documents) {
                        val itemId = doc.getString("itemId") ?: continue
                        val type = doc.getString("type") ?: "food"
                        if (type == "food") {
                            foodFavs.add(itemId)
                        } else if (type == "restaurant") {
                            restFavs.add(itemId)
                        }
                    }
                    if (foodFavs.isNotEmpty() || restFavs.isNotEmpty()) {
                        _favoriteFoodIds.value = foodFavs
                        _favoriteRestaurantIds.value = restFavs
                        prefs.edit()
                            .putStringSet("saved_favorite_foods", foodFavs)
                            .putStringSet("saved_favorite_restaurants", restFavs)
                            .apply()
                    }
                }
            }
    }

    private fun syncLocalDataToFirestore(uid: String) {
        repositoryScope.launch {
            try {
                // Sync favorite foods
                _favoriteFoodIds.value.forEach { foodId ->
                    val favDoc = db.collection("users").document(uid).collection("favorites").document("fav_f_$foodId")
                    val food = SampleData.foodItems.find { it.id == foodId }
                    favDoc.set(
                        mapOf(
                            "id" to "fav_f_$foodId",
                            "itemId" to foodId,
                            "name" to (food?.name ?: "Ethiopian Dish"),
                            "type" to "food",
                            "userId" to uid,
                            "addedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                }

                // Sync favorite restaurants
                _favoriteRestaurantIds.value.forEach { restId ->
                    val favDoc = db.collection("users").document(uid).collection("favorites").document("fav_r_$restId")
                    val rest = SampleData.restaurants.find { it.id == restId }
                    favDoc.set(
                        mapOf(
                            "id" to "fav_r_$restId",
                            "itemId" to restId,
                            "name" to (rest?.name ?: "Ethiopian Restaurant"),
                            "type" to "restaurant",
                            "userId" to uid,
                            "addedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error syncing local favorites to Firestore", e)
            }
        }
    }

    fun setLocation(location: String) {
        _selectedLocation.value = location
        prefs.edit().putString("saved_location", location).apply()
    }

    fun toggleFoodFavorite(foodId: String): Boolean {
        val current = _favoriteFoodIds.value.toMutableSet()
        val isNowFavorite = if (current.contains(foodId)) {
            current.remove(foodId)
            false
        } else {
            current.add(foodId)
            true
        }
        _favoriteFoodIds.value = current
        prefs.edit().putStringSet("saved_favorite_foods", current).apply()

        // Sync with Firestore if authenticated
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val docRef = db.collection("users").document(uid).collection("favorites").document("fav_f_$foodId")
            if (isNowFavorite) {
                val food = SampleData.foodItems.find { it.id == foodId }
                docRef.set(
                    mapOf(
                        "id" to "fav_f_$foodId",
                        "itemId" to foodId,
                        "name" to (food?.name ?: "Dish"),
                        "type" to "food",
                        "userId" to uid,
                        "addedAt" to FieldValue.serverTimestamp()
                    )
                ).addOnFailureListener { e -> Log.e(tag, "Failed to save favorite food to Firestore", e) }
            } else {
                docRef.delete()
                    .addOnFailureListener { e -> Log.e(tag, "Failed to delete favorite food from Firestore", e) }
            }
        }

        return isNowFavorite
    }

    fun toggleRestaurantFavorite(restaurantId: String): Boolean {
        val current = _favoriteRestaurantIds.value.toMutableSet()
        val isNowFavorite = if (current.contains(restaurantId)) {
            current.remove(restaurantId)
            false
        } else {
            current.add(restaurantId)
            true
        }
        _favoriteRestaurantIds.value = current
        prefs.edit().putStringSet("saved_favorite_restaurants", current).apply()

        // Sync with Firestore if authenticated
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val docRef = db.collection("users").document(uid).collection("favorites").document("fav_r_$restaurantId")
            if (isNowFavorite) {
                val rest = SampleData.restaurants.find { it.id == restaurantId }
                docRef.set(
                    mapOf(
                        "id" to "fav_r_$restaurantId",
                        "itemId" to restaurantId,
                        "name" to (rest?.name ?: "Restaurant"),
                        "type" to "restaurant",
                        "userId" to uid,
                        "addedAt" to FieldValue.serverTimestamp()
                    )
                ).addOnFailureListener { e -> Log.e(tag, "Failed to save favorite restaurant to Firestore", e) }
            } else {
                docRef.delete()
                    .addOnFailureListener { e -> Log.e(tag, "Failed to delete favorite restaurant from Firestore", e) }
            }
        }

        return isNowFavorite
    }

    fun toggleFavorite(foodId: String) {
        toggleFoodFavorite(foodId)
    }

    fun isFoodFavorite(foodId: String): Boolean {
        return _favoriteFoodIds.value.contains(foodId)
    }

    fun isRestaurantFavorite(restaurantId: String): Boolean {
        return _favoriteRestaurantIds.value.contains(restaurantId)
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
        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(8)}"

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

        // Sync to Firestore if user is authenticated
        val user = Firebase.auth.currentUser
        if (user != null) {
            val orderDocRef = db.collection("users").document(user.uid).collection("orders").document(orderId)
            val itemsData = items.map {
                mapOf(
                    "foodId" to it.foodItem.id,
                    "foodName" to it.foodItem.name,
                    "quantity" to it.quantity,
                    "price" to it.foodItem.price,
                    "spiceLevel" to it.spiceLevel
                )
            }
            val payload = mapOf(
                "id" to orderId,
                "orderNumber" to orderNumber,
                "userId" to user.uid,
                "items" to itemsData,
                "subtotal" to subtotal,
                "deliveryFee" to deliveryFee,
                "total" to total,
                "status" to OrderStatus.RECEIVED.name,
                "customerName" to newOrder.customerName,
                "customerPhone" to newOrder.customerPhone,
                "deliveryAddress" to newOrder.deliveryAddress,
                "deliveryNotes" to newOrder.deliveryNotes,
                "paymentMethod" to newOrder.paymentMethod,
                "estimatedArrival" to newOrder.estimatedArrival,
                "driverName" to newOrder.driverName,
                "driverPhone" to newOrder.driverPhone,
                "createdAt" to FieldValue.serverTimestamp()
            )
            orderDocRef.set(payload)
                .addOnFailureListener { e -> Log.e(tag, "Failed to persist order to Firestore", e) }
        }

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

        // If authenticated, update status in Firestore
        val user = Firebase.auth.currentUser
        if (user != null) {
            val updatedOrder = list.find { it.id == orderId }
            if (updatedOrder != null) {
                db.collection("users").document(user.uid).collection("orders").document(orderId)
                    .update(
                        mapOf(
                            "status" to updatedOrder.status.name,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .addOnFailureListener { e -> Log.e(tag, "Failed to update order status in Firestore", e) }
            }
        }
    }

    fun reorder(order: Order) {
        order.items.forEach { ci ->
            addToCart(ci.foodItem, ci.quantity, ci.spiceLevel, ci.specialInstructions)
        }
    }
}
