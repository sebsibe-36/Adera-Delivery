package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SampleData
import com.example.model.FoodItem
import com.example.model.Restaurant
import com.example.ui.components.FoodItemCard
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.SoftGreenContainer

@Composable
fun RestaurantScreen(
    restaurant: Restaurant,
    onBack: () -> Unit,
    onOpenFoodDetail: (FoodItem) -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedFilter by remember { mutableStateOf("All") }

    val restaurantFoods = SampleData.foodItems.filter {
        it.restaurantId == restaurant.id || it.isPopular // give a generous rich menu for each restaurant
    }

    val displayedFoods = when (selectedFilter) {
        "Fasting/Vegan" -> restaurantFoods.filter { it.isVegetarian }
        "Meat Dishes" -> restaurantFoods.filter { !it.isVegetarian }
        "Popular" -> restaurantFoods.filter { it.isPopular }
        else -> restaurantFoods
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_detail_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Cover Image with Back Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                val coverRes = restaurant.coverDrawableRes ?: R.drawable.promo_banner
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .size(42.dp)
                        .testTag("restaurant_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }

        // Restaurant Info Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = restaurant.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = restaurant.amharicName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldGreenPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = restaurant.cuisine,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftGreenContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${restaurant.rating} (${restaurant.reviewCount})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${restaurant.deliveryTimeMinutes} mins",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Delivery: ETB ${"%.0f".format(restaurant.deliveryFee)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = EmeraldGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = restaurant.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Menu Filters Row
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Menu Selection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                val filters = listOf("All", "Popular", "Fasting/Vegan", "Meat Dishes")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { f ->
                        val isSelected = f == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = f },
                            label = { Text(f, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
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

        // Food Items for this Restaurant
        items(displayedFoods) { food ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                FoodItemCard(
                    food = food,
                    onClick = { onOpenFoodDetail(food) },
                    onAddToCart = { onAddToCart(food) }
                )
            }
        }
    }
}
