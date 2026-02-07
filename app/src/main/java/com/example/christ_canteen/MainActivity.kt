package com.example.christ_canteen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFFF9800),
                    surface = Color(0xFFF5F5F5)
                )
            ) {
                // --- NAVIGATION STATE ---
                // Tracks which screen is currently visible
                var currentScreen by remember { mutableStateOf("home") }

                // Tracks the list of food added to the cart
                var currentCartItems by remember { mutableStateOf(listOf<FoodItem>()) }

                // Tracks the active order ID for the status screen
                var currentOrderId by remember { mutableStateOf("") }

                // --- SCREEN SWITCHING LOGIC ---
                when (currentScreen) {
                    "home" -> {
                        LandingScreen(
                            onOutletClick = { outletId ->
                                // Only "Mingos" (ID 1) works for this demo
                                if (outletId == "1") {
                                    currentScreen = "menu"
                                }
                            }
                        )
                    }

                    "menu" -> {
                        MenuScreen(
                            onBackClick = { currentScreen = "home" },
                            onViewCart = { selectedItems ->
                                // Save the items and go to Summary
                                currentCartItems = selectedItems
                                currentScreen = "summary"
                            }
                        )
                    }

                    "summary" -> {
                        OrderSummaryScreen(
                            cartItems = currentCartItems,
                            onBackClick = { currentScreen = "menu" },
                            onOrderSuccess = { newOrderId ->
                                // Order placed successfully!
                                // Save the ID and go to Live Tracking
                                currentOrderId = newOrderId
                                currentScreen = "status"
                            }
                        )
                    }

                    "status" -> {
                        LiveStatusScreen(
                            orderId = currentOrderId,
                            onHomeClick = {
                                // Clear cart and go home for next order
                                currentCartItems = emptyList()
                                currentScreen = "home"
                            }
                        )
                    }
                }
            }
        }
    }
}