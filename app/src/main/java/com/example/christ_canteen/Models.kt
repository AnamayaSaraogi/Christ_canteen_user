package com.example.christ_canteen

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import java.util.Date

// --- DATA CLASSES ---
data class Outlet(
    val id: String,
    val name: String,
    val category: String,
    val rating: String,
    val time: String,
    val color: Color,
    val isOpen: Boolean = true
)

data class FoodItem(
    val id: String,
    val name: String,
    val price: Int,
    val color: Color,
    val category: String
)

// --- MOCK DATA ---
val OUTLETS = listOf(
    Outlet("1", "Mingos", "Burgers & Shakes • Premium", "4.8", "15-20 mins", Color(0xFFFF9800)), // Orange
    Outlet("2", "Punjabi Bites", "North Indian • Thali", "4.5", "10-15 mins", Color(0xFFE91E63)), // Pink
    Outlet("3", "Micheals", "Bakery • Pastries • Coffee", "4.9", "5-10 mins", Color(0xFF795548)), // Brown
    Outlet("4", "Nandini", "Milk • Snacks • Beverages", "4.2", "0-5 mins", Color(0xFF2196F3))   // Blue
)

val MINGOS_MENU = listOf(
    FoodItem("1", "Cheese Burger", 149, Color(0xFF795548), "Burgers"),
    FoodItem("2", "Veg Pizza", 299, Color(0xFFFF5722), "Pizza"),
    FoodItem("3", "Masala Dosa", 80, Color(0xFFFFC107), "South Indian"),
    FoodItem("4", "Iced Coffee", 120, Color(0xFF8D6E63), "Beverages"),
    FoodItem("5", "Club Sandwich", 110, Color(0xFF4CAF50), "Sandwich"),
    FoodItem("6", "Penne Pasta", 210, Color(0xFFE91E63), "Pasta")
)

// ... (Your Outlet and FoodItem classes are above this) ...

// --- ORDER MODEL FOR FIREBASE ---
data class Order(
    val orderId: String = "",
    val studentName: String = "Student",
    val itemName: String = "",
    val outletName: String = "",
    val price: Int = 0,
    val status: String = "PENDING", // PENDING, PREPARING, READY
    val timestamp: Date = Date(),
    val predictedTime: Int = 15
)