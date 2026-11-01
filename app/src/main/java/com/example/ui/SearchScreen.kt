package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.FoodItem
import com.example.model.Restaurant
import com.example.ui.components.FoodItemCard
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.SpicyRed
import com.example.ui.theme.VegetarianGreen

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategoryId: String,
    onCategoryChange: (String) -> Unit,
    isVegOnly: Boolean,
    onToggleVeg: () -> Unit,
    isSpicyOnly: Boolean,
    onToggleSpicy: () -> Unit,
    onOpenRestaurant: (Restaurant) -> Unit,
    onOpenFoodDetail: (FoodItem) -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    favoriteFoodIds: Set<String> = emptySet(),
    favoriteRestaurantIds: Set<String> = emptySet(),
    onToggleFoodFavorite: (String) -> Unit = {},
    onToggleRestaurantFavorite: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Foods", "Restaurants")

    // Filter logic
    val matchingFoods = SampleData.foodItems.filter { food ->
        val matchesQuery = query.isBlank() ||
                food.name.contains(query, ignoreCase = true) ||
                food.amharicName.contains(query, ignoreCase = true) ||
                food.description.contains(query, ignoreCase = true) ||
                food.restaurantName.contains(query, ignoreCase = true)

        val matchesCategory = selectedCategoryId == "cat_all" || food.categoryId == selectedCategoryId
        val matchesVeg = !isVegOnly || food.isVegetarian
        val matchesSpicy = !isSpicyOnly || food.spicyLevel >= 2

        matchesQuery && matchesCategory && matchesVeg && matchesSpicy
    }

    val matchingRestaurants = SampleData.restaurants.filter { rest ->
        val matchesQuery = query.isBlank() ||
                rest.name.contains(query, ignoreCase = true) ||
                rest.amharicName.contains(query, ignoreCase = true) ||
                rest.cuisine.contains(query, ignoreCase = true) ||
                rest.tags.any { it.contains(query, ignoreCase = true) }

        matchesQuery
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Search Input Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_input"),
                    placeholder = {
                        Text(
                            "Search foods, restaurants, tibs, doro wat...",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = EmeraldGreenPrimary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }
        }

        // Quick Suggestions if query is empty
        if (query.isEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "Popular Searches",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val suggestions = listOf("Doro Wat", "Shekla Tibs", "Beyaynet", "Kitfo", "Shiro", "Chechebsa")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(suggestions) { s ->
                            AssistChip(
                                onClick = { onQueryChange(s) },
                                label = { Text(s, fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Category & Dietary Filter Chips Row
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = isVegOnly,
                            onClick = onToggleVeg,
                            label = { Text("Fasting / Vegan") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = if (isVegOnly) Color.White else VegetarianGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VegetarianGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    item {
                        FilterChip(
                            selected = isSpicyOnly,
                            onClick = onToggleSpicy,
                            label = { Text("Spicy") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = if (isSpicyOnly) Color.White else SpicyRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SpicyRed,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    items(SampleData.categories) { cat ->
                        val isSelected = cat.id == selectedCategoryId
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryChange(cat.id) },
                            label = { Text(cat.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        // Tabs: All / Foods / Restaurants
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = EmeraldGreenPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { idx, title ->
                    val count = when (idx) {
                        0 -> matchingFoods.size + matchingRestaurants.size
                        1 -> matchingFoods.size
                        else -> matchingRestaurants.size
                    }
                    Tab(
                        selected = selectedTabIndex == idx,
                        onClick = { selectedTabIndex = idx },
                        text = {
                            Text(
                                text = "$title ($count)",
                                fontWeight = if (selectedTabIndex == idx) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == idx) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // Empty state handling
        if (matchingFoods.isEmpty() && matchingRestaurants.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results found for \"$query\"",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try searching for Tibs, Shiro, Kitfo or Addis Habesha",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Results Display
        if (selectedTabIndex == 0 || selectedTabIndex == 2) {
            if (matchingRestaurants.isNotEmpty()) {
                item {
                    Text(
                        text = "Restaurants (${matchingRestaurants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
                    )
                }
                items(matchingRestaurants) { rest ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        RestaurantCard(
                            restaurant = rest,
                            onClick = { onOpenRestaurant(rest) },
                            isFavorite = favoriteRestaurantIds.contains(rest.id),
                            onToggleFavorite = { onToggleRestaurantFavorite(rest.id) }
                        )
                    }
                }
            }
        }

        if (selectedTabIndex == 0 || selectedTabIndex == 1) {
            if (matchingFoods.isNotEmpty()) {
                item {
                    Text(
                        text = "Dishes & Food Items (${matchingFoods.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
                    )
                }
                items(matchingFoods) { food ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        FoodItemCard(
                            food = food,
                            onClick = { onOpenFoodDetail(food) },
                            onAddToCart = { onAddToCart(food) },
                            isFavorite = favoriteFoodIds.contains(food.id),
                            onToggleFavorite = { onToggleFoodFavorite(food.id) }
                        )
                    }
                }
            }
        }
    }
}
