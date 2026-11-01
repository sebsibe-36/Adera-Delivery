package com.example.model

enum class OrderStatus(val stepIndex: Int, val label: String, val description: String) {
    RECEIVED(0, "Order Received", "Restaurant accepted your order"),
    PREPARING(1, "Preparing Food", "Chef is cooking authentic fresh dishes"),
    READY_FOR_DELIVERY(2, "Ready for Delivery", "Driver picked up food and is on the way"),
    DELIVERED(3, "Delivered", "Delivered safely. Enjoy your meal!")
}

data class Category(
    val id: String,
    val name: String,
    val amharicName: String,
    val iconName: String,
    val itemCount: Int
)

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val name: String,
    val amharicName: String,
    val description: String,
    val price: Double, // in ETB
    val categoryId: String,
    val drawableRes: Int? = null,
    val rating: Double = 4.8,
    val reviewCount: Int = 120,
    val spicyLevel: Int = 1, // 0: None, 1: Mild, 2: Medium, 3: Hot
    val isVegetarian: Boolean = false,
    val isPopular: Boolean = false,
    val preparationTime: String = "15-25 min",
    val portionInfo: String = "Serves 1-2"
)

data class Restaurant(
    val id: String,
    val name: String,
    val amharicName: String,
    val cuisine: String,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeMinutes: Int,
    val deliveryFee: Double, // in ETB
    val minOrder: Double,
    val address: String,
    val coverDrawableRes: Int? = null,
    val tags: List<String>,
    val isFeatured: Boolean = false
)

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int = 1,
    val spiceLevel: String = "Medium",
    val specialInstructions: String = ""
) {
    val totalPrice: Double
        get() = foodItem.price * quantity
}

data class Order(
    val id: String,
    val orderNumber: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double = 0.0,
    val total: Double,
    val status: OrderStatus,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryNotes: String = "",
    val paymentMethod: String = "Cash on Delivery",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val estimatedArrival: String = "30-40 mins",
    val driverName: String = "Abebe Kebede",
    val driverPhone: String = "+251 91 123 4567"
)
