package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.theme.DarkGrayText
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.LightGreenSecondary
import com.example.ui.theme.SoftGreenContainer

@Composable
fun OrderTrackingScreen(
    orders: List<Order>,
    activeOrderId: String?,
    onAdvanceOrderStatus: (String) -> Unit,
    onSelectOrder: (String) -> Unit,
    onReorder: (Order) -> Unit,
    onExploreFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active Tracker", "Order History")

    val currentOrder = orders.find { it.id == activeOrderId } ?: orders.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("orders_tracking_screen")
    ) {
        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EmeraldGreenPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == index) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = EmeraldGreenPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Orders Yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When you order your favorite Ethiopian food, you can track it live here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreFood,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Find Food", color = Color.White)
                    }
                }
            }
        } else if (selectedTab == 0 && currentOrder != null) {
            // Active Order Live Tracking View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("live_order_tracker"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Header with Order ID and Status Pill
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftGreenContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Order #${currentOrder.orderNumber}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreenDark
                                    )
                                    Text(
                                        text = "Estimated Arrival: ${currentOrder.estimatedArrival}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EmeraldGreenPrimary)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = currentOrder.status.label,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 4-Stage Live Tracking Stepper
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Live Order Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val steps = listOf(
                                Triple(OrderStatus.RECEIVED, "1. Order Received", Icons.Default.ReceiptLong),
                                Triple(OrderStatus.PREPARING, "2. Preparing Food", Icons.Default.OutdoorGrill),
                                Triple(OrderStatus.READY_FOR_DELIVERY, "3. Ready for Delivery", Icons.Default.DeliveryDining),
                                Triple(OrderStatus.DELIVERED, "4. Delivered", Icons.Default.CheckCircle)
                            )

                            steps.forEachIndexed { idx, (status, label, icon) ->
                                val isPassed = currentOrder.status.stepIndex >= status.stepIndex
                                val isCurrent = currentOrder.status == status

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isPassed) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isPassed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isPassed) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = status.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isPassed) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = EmeraldGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                if (idx < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 18.dp)
                                            .width(2.dp)
                                            .height(24.dp)
                                            .background(
                                                if (currentOrder.status.stepIndex > status.stepIndex) EmeraldGreenPrimary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Demo Test: Advance Status button
                            Button(
                                onClick = { onAdvanceOrderStatus(currentOrder.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("advance_order_status_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Loop, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentOrder.status == OrderStatus.DELIVERED) "Reset Order Simulation"
                                    else "Advance Status (Simulate Next Step)"
                                )
                            }
                        }
                    }
                }

                // Delivery Driver Info
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SoftGreenContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeliveryDining,
                                        contentDescription = null,
                                        tint = EmeraldGreenPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentOrder.driverName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Adera Delivery Courier",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(EmeraldGreenPrimary)
                                    .padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Courier",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Ordered Food Items List
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ordered Food Items (${currentOrder.items.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            currentOrder.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.foodItem.name} (${item.spiceLevel})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "ETB ${"%.0f".format(item.totalPrice)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Address", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(currentOrder.deliveryAddress, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Method", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(currentOrder.paymentMethod, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Paid", fontWeight = FontWeight.Bold)
                                Text(
                                    "ETB ${"%.0f".format(currentOrder.total)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldGreenPrimary
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        } else {
            // Orders History List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("orders_history_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onSelectOrder(order.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.orderNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (order.status == OrderStatus.DELIVERED) SoftGreenContainer else GoldAccent.copy(alpha = 0.2f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = order.status.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (order.status == OrderStatus.DELIVERED) EmeraldGreenPrimary else GoldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = order.items.joinToString(", ") { "${it.quantity}x ${it.foodItem.name}" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total: ETB ${"%.0f".format(order.total)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldGreenPrimary
                                )

                                Row {
                                    OutlinedButton(
                                        onClick = { onReorder(order) },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Reorder", fontSize = 12.sp, color = EmeraldGreenPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onSelectOrder(order.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                                    ) {
                                        Text("Track", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
