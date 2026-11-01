package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.FoodItemCard
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.DarkGrayText
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LightGreenSecondary
import com.example.ui.theme.SoftGreenContainer
import com.example.viewmodel.ScreenTab

@Composable
fun HomeScreen(
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    onOpenRestaurant: (Restaurant) -> Unit,
    onOpenFoodDetail: (FoodItem) -> Unit,
    onAddToCart: (FoodItem) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredFoods = if (selectedCategoryId == "cat_all") {
        SampleData.foodItems
    } else {
        SampleData.foodItems.filter { it.categoryId == selectedCategoryId }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Search trigger bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onNavigateToSearch() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("home_search_trigger")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EmeraldGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search Ethiopian foods, tibs, doro wat...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Promotional Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("promotional_banner_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.promo_banner),
                        contentDescription = "Promotional Feast",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccent)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = DarkGrayText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "20% OFF FIRST ORDER",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = DarkGrayText
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Authentic Taste\nof Ethiopia",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hot Doro Wat, Sizzling Tibs & Injera to your door",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Food Categories Horizontal Scroll
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Food Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SampleData.categories.forEach { cat ->
                        val isSelected = cat.id == selectedCategoryId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onCategorySelected(cat.id) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("cat_button_${cat.id}")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cat.amharicName,
                                    fontSize = 10.sp,
                                    color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Popular Restaurants Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Popular Restaurants",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Top rated kitchens in Addis Ababa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Horizontal Popular Restaurants Row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(SampleData.restaurants.take(4)) { rest ->
                    RestaurantCard(
                        restaurant = rest,
                        onClick = { onOpenRestaurant(rest) },
                        modifier = Modifier.width(260.dp)
                    )
                }
            }
        }

        // Recommended / Category Food Items Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedCategoryId == "cat_all") "Recommended Dishes" else "Menu Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${filteredFoods.size} items available right now",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Recommended Food Items Grid / Vertical List
        items(filteredFoods) { food ->
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
