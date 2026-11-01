package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SampleData
import com.example.ui.components.AppHeader
import com.example.ui.components.LocationSelectorModal
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftGreenContainer
import com.example.auth.AuthManager
import com.example.ui.components.LoginDialog
import com.example.viewmodel.AderaViewModel
import com.example.viewmodel.NavigationScreen
import com.example.viewmodel.ScreenTab

@Composable
fun AderaApp(
    viewModel: AderaViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedRestaurant by viewModel.selectedRestaurant.collectAsStateWithLifecycle()
    val detailFoodItem by viewModel.detailFoodItem.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartCount.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val activeOrderId by viewModel.activeOrderId.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val showLocationPicker by viewModel.showLocationPicker.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val favoriteFoodIds by viewModel.favoriteFoodIds.collectAsStateWithLifecycle()
    val favoriteRestaurantIds by viewModel.favoriteRestaurantIds.collectAsStateWithLifecycle()
    val favoriteFoods by viewModel.favoriteFoods.collectAsStateWithLifecycle()
    val favoriteRestaurants by viewModel.favoriteRestaurants.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val isVegOnly by viewModel.isVegOnlyFilter.collectAsStateWithLifecycle()
    val isSpicyOnly by viewModel.isSpicyOnlyFilter.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isCloudSynced by viewModel.isCloudSynced.collectAsStateWithLifecycle()
    val showLoginDialog by viewModel.showLoginDialog.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val credentialManager = remember { androidx.credentials.CredentialManager.create(context) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Safety guard: if logged out while on checkout screen, return to main tabs
    LaunchedEffect(currentUser, currentScreen) {
        if (currentUser == null && currentScreen == NavigationScreen.CHECKOUT) {
            viewModel.navigateBack()
        }
    }

    // Top-level back handler
    BackHandler(enabled = currentScreen != NavigationScreen.MAIN_TABS || currentTab != ScreenTab.HOME) {
        if (currentScreen != NavigationScreen.MAIN_TABS) {
            viewModel.navigateBack()
        } else if (currentTab != ScreenTab.HOME) {
            viewModel.selectTab(ScreenTab.HOME)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen == NavigationScreen.MAIN_TABS) {
                AppHeader(
                    location = selectedLocation,
                    onLocationClick = { viewModel.openLocationPicker() }
                )
            }
        },
        bottomBar = {
            if (currentScreen == NavigationScreen.MAIN_TABS) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("adera_bottom_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    // 1. Home
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.HOME,
                        onClick = { viewModel.selectTab(ScreenTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreenPrimary,
                            selectedTextColor = EmeraldGreenPrimary,
                            indicatorColor = SoftGreenContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    // 2. Search
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.SEARCH,
                        onClick = { viewModel.selectTab(ScreenTab.SEARCH) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "Search"
                            )
                        },
                        label = { Text("Search", fontWeight = if (currentTab == ScreenTab.SEARCH) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreenPrimary,
                            selectedTextColor = EmeraldGreenPrimary,
                            indicatorColor = SoftGreenContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_search")
                    )

                    // 3. Cart
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.CART,
                        onClick = { viewModel.selectTab(ScreenTab.CART) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(
                                            containerColor = EmeraldGreenPrimary,
                                            contentColor = Color.White
                                        ) {
                                            Text("$cartCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = "Cart"
                                )
                            }
                        },
                        label = { Text("Cart", fontWeight = if (currentTab == ScreenTab.CART) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreenPrimary,
                            selectedTextColor = EmeraldGreenPrimary,
                            indicatorColor = SoftGreenContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_cart")
                    )

                    // 4. Orders
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.ORDERS,
                        onClick = { viewModel.selectTab(ScreenTab.ORDERS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.ORDERS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                contentDescription = "Orders"
                            )
                        },
                        label = { Text("Orders", fontWeight = if (currentTab == ScreenTab.ORDERS) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreenPrimary,
                            selectedTextColor = EmeraldGreenPrimary,
                            indicatorColor = SoftGreenContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_orders")
                    )

                    // 5. Profile
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.PROFILE,
                        onClick = { viewModel.selectTab(ScreenTab.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontWeight = if (currentTab == ScreenTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreenPrimary,
                            selectedTextColor = EmeraldGreenPrimary,
                            indicatorColor = SoftGreenContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                NavigationScreen.FAVORITES -> {
                    FavoritesScreen(
                        favoriteFoods = favoriteFoods,
                        favoriteRestaurants = favoriteRestaurants,
                        onBack = { viewModel.navigateBack() },
                        onOpenFoodDetail = { viewModel.openFoodDetail(it) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onOpenRestaurant = { viewModel.openRestaurant(it) },
                        onToggleFoodFavorite = { viewModel.toggleFoodFavorite(it) },
                        onToggleRestaurantFavorite = { viewModel.toggleRestaurantFavorite(it) },
                        onExploreFoods = {
                            viewModel.navigateBack()
                            viewModel.selectTab(ScreenTab.HOME)
                        }
                    )
                }

                NavigationScreen.RESTAURANT_DETAIL -> {
                    selectedRestaurant?.let { rest ->
                        RestaurantScreen(
                            restaurant = rest,
                            onBack = { viewModel.navigateBack() },
                            onOpenFoodDetail = { viewModel.openFoodDetail(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            isFavoriteRestaurant = favoriteRestaurantIds.contains(rest.id),
                            onToggleRestaurantFavorite = { viewModel.toggleRestaurantFavorite(rest.id) },
                            favoriteFoodIds = favoriteFoodIds,
                            onToggleFoodFavorite = { viewModel.toggleFoodFavorite(it) }
                        )
                    }
                }

                NavigationScreen.CHECKOUT -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        total = cartTotal,
                        initialLocation = selectedLocation,
                        onBack = { viewModel.navigateBack() },
                        onPlaceOrder = { name, phone, address, notes, payment ->
                            viewModel.placeOrder(name, phone, address, notes, payment)
                        },
                        currentUser = currentUser
                    )
                }

                NavigationScreen.MAIN_TABS -> {
                    when (currentTab) {
                        ScreenTab.HOME -> {
                            HomeScreen(
                                selectedCategoryId = selectedCategoryFilter,
                                onCategorySelected = { viewModel.setCategoryFilter(it) },
                                onOpenRestaurant = { viewModel.openRestaurant(it) },
                                onOpenFoodDetail = { viewModel.openFoodDetail(it) },
                                onAddToCart = { viewModel.addToCart(it) },
                                onNavigateToSearch = { viewModel.selectTab(ScreenTab.SEARCH) },
                                favoriteFoodIds = favoriteFoodIds,
                                favoriteRestaurantIds = favoriteRestaurantIds,
                                onToggleFoodFavorite = { viewModel.toggleFoodFavorite(it) },
                                onToggleRestaurantFavorite = { viewModel.toggleRestaurantFavorite(it) }
                            )
                        }

                        ScreenTab.SEARCH -> {
                            SearchScreen(
                                query = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                selectedCategoryId = selectedCategoryFilter,
                                onCategoryChange = { viewModel.setCategoryFilter(it) },
                                isVegOnly = isVegOnly,
                                onToggleVeg = { viewModel.toggleVegFilter() },
                                isSpicyOnly = isSpicyOnly,
                                onToggleSpicy = { viewModel.toggleSpicyFilter() },
                                onOpenRestaurant = { viewModel.openRestaurant(it) },
                                onOpenFoodDetail = { viewModel.openFoodDetail(it) },
                                onAddToCart = { viewModel.addToCart(it) },
                                favoriteFoodIds = favoriteFoodIds,
                                favoriteRestaurantIds = favoriteRestaurantIds,
                                onToggleFoodFavorite = { viewModel.toggleFoodFavorite(it) },
                                onToggleRestaurantFavorite = { viewModel.toggleRestaurantFavorite(it) }
                            )
                        }

                        ScreenTab.CART -> {
                            CartScreen(
                                cartItems = cartItems,
                                subtotal = cartSubtotal,
                                onUpdateQuantity = { id, spice, qty ->
                                    viewModel.updateCartQuantity(id, spice, qty)
                                },
                                onRemoveItem = { id, spice ->
                                    viewModel.removeFromCart(id, spice)
                                },
                                onClearCart = { viewModel.clearCart() },
                                onProceedToCheckout = { viewModel.navigateToCheckout() },
                                onExploreFood = { viewModel.selectTab(ScreenTab.HOME) },
                                isLoggedIn = currentUser != null,
                                onSignInClick = { viewModel.openLoginDialog() }
                            )
                        }

                        ScreenTab.ORDERS -> {
                            OrderTrackingScreen(
                                orders = orders,
                                activeOrderId = activeOrderId,
                                onAdvanceOrderStatus = { viewModel.advanceOrderStatus(it) },
                                onSelectOrder = { viewModel.setActiveOrder(it) },
                                onReorder = { viewModel.reorder(it) },
                                onExploreFood = { viewModel.selectTab(ScreenTab.HOME) }
                            )
                        }

                        ScreenTab.PROFILE -> {
                            ProfileScreen(
                                currentLocation = selectedLocation,
                                onOpenLocationPicker = { viewModel.openLocationPicker() },
                                onNavigateToOrders = { viewModel.selectTab(ScreenTab.ORDERS) },
                                favoritesCount = favoriteFoodIds.size + favoriteRestaurantIds.size,
                                onNavigateToFavorites = { viewModel.navigateToFavorites() },
                                currentUser = currentUser,
                                isCloudSynced = isCloudSynced,
                                onSignInClick = { viewModel.openLoginDialog() },
                                onSignOutClick = {
                                    AuthManager.signOut(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onSignOutComplete = { viewModel.handleUserSignedOut() },
                                        scope = scope
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Food Detail Modal Sheet
            detailFoodItem?.let { food ->
                FoodDetailModal(
                    food = food,
                    onDismiss = { viewModel.closeFoodDetail() },
                    onAddToCart = { item, qty, spice, notes ->
                        viewModel.addToCart(item, qty, spice, notes)
                    },
                    isFavorite = favoriteFoodIds.contains(food.id),
                    onToggleFavorite = { viewModel.toggleFoodFavorite(food.id) }
                )
            }

            // Location Selector Dialog
            if (showLocationPicker) {
                LocationSelectorModal(
                    locations = SampleData.deliveryLocations,
                    currentLocation = selectedLocation,
                    onSelectLocation = { viewModel.selectLocation(it) },
                    onDismiss = { viewModel.closeLocationPicker() }
                )
            }

            // Google Login Dialog
            if (showLoginDialog) {
                LoginDialog(
                    onDismiss = { viewModel.closeLoginDialog() },
                    onAuthSuccess = { user -> viewModel.handleUserSignedIn(user) }
                )
            }
        }
    }
}
