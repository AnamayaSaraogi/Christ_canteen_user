package com.example.christ_canteen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onBackClick: () -> Unit,
    onViewCart: (List<FoodItem>) -> Unit // UPDATED: Pass the list
) {
    var selectedCategory by remember { mutableStateOf("All") }
    // We now track the actual LIST of items
    val cartItems = remember { mutableStateListOf<FoodItem>() }

    val filteredItems = if (selectedCategory == "All") MINGOS_MENU else MINGOS_MENU.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mingos Menu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        },
        floatingActionButton = {
            if (cartItems.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { onViewCart(cartItems) }, // Pass list to next screen
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                ) {
                    Text("View Cart • ${cartItems.size} items", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            // Category Filter
            val categories = listOf("All", "Burgers", "Pizza", "Beverages")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = cat == selectedCategory,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        enabled = true,
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFF9800))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { item ->
                    FoodItemCard(item, onAdd = {
                        cartItems.add(item) // Add to list
                    })
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(item: FoodItem, onAdd: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(item.color), contentAlignment = Alignment.Center) {
                Text(item.name.take(2), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("₹${item.price}", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    IconButton(onClick = onAdd, modifier = Modifier.size(24.dp).background(Color(0xFFFF9800), CircleShape)) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}