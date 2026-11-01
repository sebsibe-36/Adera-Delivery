package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Category
import com.example.model.FoodItem
import com.example.model.Restaurant
import com.example.ui.theme.DarkGrayText
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.LightGreenSecondary
import com.example.ui.theme.SoftGreenContainer
import com.example.ui.theme.SpicyRed
import com.example.ui.theme.VegetarianGreen
import com.example.viewmodel.ScreenTab

@Composable
fun AppHeader(
    location: String,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLocationClick() }
                    .padding(4.dp)
                    .testTag("location_picker_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SoftGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = EmeraldGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DELIVERING TO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Change Location",
                            tint = EmeraldGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Brand Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EmeraldGreenPrimary)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Adera",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    food: FoodItem,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("food_item_card_${food.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val imgRes = food.drawableRes ?: R.drawable.promo_banner
                Image(
                    painter = painterResource(id = imgRes),
                    contentDescription = food.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )

                // Dietary and Popular Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (food.isVegetarian) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VegetarianGreen)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Eco,
                                    contentDescription = "Vegetarian",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Fasting/Vegan",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (food.isPopular) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccent)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Popular",
                                color = DarkGrayText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = food.amharicName,
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ETB ${"%.0f".format(food.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldGreenPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = GoldDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${food.rating}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreenPrimary)
                            .testTag("quick_add_button_${food.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add ${food.name} to cart",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("restaurant_card_${restaurant.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val coverRes = restaurant.coverDrawableRes ?: R.drawable.promo_banner
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )

                // Delivery Time pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${restaurant.deliveryTimeMinutes} min",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = EmeraldGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${restaurant.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenPrimary
                        )
                    }
                }

                Text(
                    text = "${restaurant.amharicName} • ${restaurant.cuisine}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Delivery: ETB ${"%.0f".format(restaurant.deliveryFee)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = restaurant.address,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = cat.id == selectedCategoryId
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(cat.id) },
                label = {
                    Text(
                        text = cat.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldGreenPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("category_chip_${cat.id}")
            )
        }
    }
}

@Composable
fun QuantityController(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SoftGreenContainer)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(32.dp).testTag("quantity_decrease_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = EmeraldGreenPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = "$quantity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = EmeraldGreenPrimary,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(32.dp).testTag("quantity_increase_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = EmeraldGreenPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LocationSelectorModal(
    locations: List<String>,
    currentLocation: String,
    onSelectLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Delivery Area",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                locations.forEach { loc ->
                    val isSelected = loc == currentLocation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) SoftGreenContainer else Color.Transparent
                            )
                            .clickable { onSelectLocation(loc) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = EmeraldGreenPrimary)
            }
        }
    )
}
