package com.example.christ_canteen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(onOutletClick: (String) -> Unit) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F9F9))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CAMPUS LOCATION", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("Christ University", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search
            SearchBar(
                query = "", onQueryChange = {}, onSearch = {}, active = false, onActiveChange = {},
                placeholder = { Text("Search outlets...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = SearchBarDefaults.colors(containerColor = Color.White)
            ) {}

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            val categories = listOf("All", "Fast Food", "Beverages", "Desi")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == "All"
                    FilterChip(
                        selected = isSelected, onClick = { }, label = { Text(cat) }, enabled = true,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF9800),
                            containerColor = Color.White
                        ),
                        border = null // Removed to prevent crashes
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Outlets List
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { Text("Available Outlets", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

                // FEATURED CARD (Mingos)
                item {
                    FeaturedOutletCard(OUTLETS[0], onClick = { onOutletClick("1") })
                }

                // OTHER OUTLETS
                items(OUTLETS.drop(1)) { outlet ->
                    SmallOutletCard(outlet)
                }
            }
        }
    }
}

@Composable
fun FeaturedOutletCard(outlet: Outlet, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(260.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(outlet.color)) {
                Text("FEATURED", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(outlet.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(outlet.category, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(" ${outlet.rating} • ${outlet.time}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SmallOutletCard(outlet: Outlet) {
    Card(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(outlet.color))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(outlet.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(outlet.category, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = true, onClick = {})
        // Using Icons.Default.List instead of Receipt to prevent crash
        NavigationBarItem(icon = { Icon(Icons.Default.List, null) }, label = { Text("Orders") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = false, onClick = {})
    }
}