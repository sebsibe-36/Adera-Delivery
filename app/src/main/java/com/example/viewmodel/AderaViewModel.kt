package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AderaRepository
import com.example.data.SampleData
import com.example.model.CartItem
import com.example.model.Category
import com.example.model.FoodItem
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    SEARCH,
    CART,
    ORDERS,
    PROFILE
}

enum class NavigationScreen {
    MAIN_TABS,
    RESTAURANT_DETAIL,
    CHECKOUT
}

class AderaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AderaRepository(application.applicationContext)

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val orders: StateFlow<List<Order>> = repository.orders
    val activeOrderId: StateFlow<String?> = repository.activeOrderId
    val selectedLocation: StateFlow<String> = repository.selectedLocation
    val favorites: StateFlow<Set<String>> = repository.favorites

    val cartCount: StateFlow<Int> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = cartSubtotal.combine(cartSubtotal) { subtotal, _ ->
        if (subtotal > 0) subtotal + 50.0 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Navigation state
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _currentScreen = MutableStateFlow(NavigationScreen.MAIN_TABS)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()

    private val _detailFoodItem = MutableStateFlow<FoodItem?>(null)
    val detailFoodItem: StateFlow<FoodItem?> = _detailFoodItem.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("cat_all")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _isVegOnlyFilter = MutableStateFlow(false)
    val isVegOnlyFilter: StateFlow<Boolean> = _isVegOnlyFilter.asStateFlow()

    private val _isSpicyOnlyFilter = MutableStateFlow(false)
    val isSpicyOnlyFilter: StateFlow<Boolean> = _isSpicyOnlyFilter.asStateFlow()

    // Snack / Notification message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Location Picker Dialog
    private val _showLocationPicker = MutableStateFlow(false)
    val showLocationPicker: StateFlow<Boolean> = _showLocationPicker.asStateFlow()

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
        _currentScreen.value = NavigationScreen.MAIN_TABS
    }

    fun openRestaurant(restaurant: Restaurant) {
        _selectedRestaurant.value = restaurant
        _currentScreen.value = NavigationScreen.RESTAURANT_DETAIL
    }

    fun openFoodDetail(foodItem: FoodItem) {
        _detailFoodItem.value = foodItem
    }

    fun closeFoodDetail() {
        _detailFoodItem.value = null
    }

    fun navigateToCheckout() {
        if (cartItems.value.isNotEmpty()) {
            _currentScreen.value = NavigationScreen.CHECKOUT
        } else {
            showToast("Your cart is empty! Add some delicious dishes first.")
        }
    }

    fun navigateBack() {
        if (_currentScreen.value != NavigationScreen.MAIN_TABS) {
            _currentScreen.value = NavigationScreen.MAIN_TABS
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(categoryId: String) {
        _selectedCategoryFilter.value = categoryId
    }

    fun toggleVegFilter() {
        _isVegOnlyFilter.value = !_isVegOnlyFilter.value
    }

    fun toggleSpicyFilter() {
        _isSpicyOnlyFilter.value = !_isSpicyOnlyFilter.value
    }

    fun toggleFavorite(foodId: String) {
        repository.toggleFavorite(foodId)
    }

    fun openLocationPicker() {
        _showLocationPicker.value = true
    }

    fun closeLocationPicker() {
        _showLocationPicker.value = false
    }

    fun selectLocation(location: String) {
        repository.setLocation(location)
        _showLocationPicker.value = false
        showToast("Delivery location set to $location")
    }

    fun addToCart(foodItem: FoodItem, quantity: Int = 1, spiceLevel: String = "Medium", instructions: String = "") {
        repository.addToCart(foodItem, quantity, spiceLevel, instructions)
        showToast("Added ${quantity}x ${foodItem.name} to cart!")
        _detailFoodItem.value = null
    }

    fun updateCartQuantity(foodItemId: String, spiceLevel: String, newQuantity: Int) {
        repository.updateCartQuantity(foodItemId, spiceLevel, newQuantity)
    }

    fun removeFromCart(foodItemId: String, spiceLevel: String) {
        repository.removeFromCart(foodItemId, spiceLevel)
        showToast("Item removed from cart")
    }

    fun clearCart() {
        repository.clearCart()
        showToast("Cart cleared")
    }

    fun placeOrder(
        name: String,
        phone: String,
        address: String,
        notes: String,
        paymentMethod: String
    ): Order {
        val newOrder = repository.placeOrder(name, phone, address, notes, paymentMethod)
        _currentScreen.value = NavigationScreen.MAIN_TABS
        _currentTab.value = ScreenTab.ORDERS
        showToast("Order #${newOrder.orderNumber} placed successfully!")
        return newOrder
    }

    fun advanceOrderStatus(orderId: String) {
        repository.advanceOrderStatus(orderId)
    }

    fun setActiveOrder(orderId: String) {
        repository.setActiveOrder(orderId)
        _currentTab.value = ScreenTab.ORDERS
        _currentScreen.value = NavigationScreen.MAIN_TABS
    }

    fun reorder(order: Order) {
        repository.reorder(order)
        _currentTab.value = ScreenTab.CART
        _currentScreen.value = NavigationScreen.MAIN_TABS
        showToast("Added items from #${order.orderNumber} to cart")
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
