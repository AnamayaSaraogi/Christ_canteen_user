package com.example.christ_canteen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(
    cartItems: List<FoodItem>,
    onBackClick: () -> Unit,
    onOrderSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var isPlacingOrder by remember { mutableStateOf(false) }

    // --- QUEUE PREDICTION STATE ---
    var activeOrdersCount by remember { mutableStateOf(0) }
    var predictedTime by remember { mutableStateOf(15) } // Default fallback
    var isLoadingPrediction by remember { mutableStateOf(true) }

    val totalAmount = cartItems.sumOf { it.price }
    val tax = 5
    val finalTotal = totalAmount + tax

    // --- 1. FETCH QUEUE SIZE ON LOAD ---
    LaunchedEffect(Unit) {
        // "Active" means Pending or Preparing
        db.collection("orders")
            .whereIn("status", listOf("PENDING", "PREPARING"))
            .get()
            .addOnSuccessListener { result ->
                // How many people are waiting right now?
                activeOrdersCount = result.size()

                // Calculate Prediction
                predictedTime = PredictionEngine.calculateWaitTime(activeOrdersCount)
                isLoadingPrediction = false
            }
            .addOnFailureListener {
                isLoadingPrediction = false // Fail gracefully
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Order Summary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        bottomBar = {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // --- PREDICTION DISPLAY ---
                    if (isLoadingPrediction) {
                        Text("Calculating wait time...", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Queue: $activeOrdersCount ahead • Wait: ~$predictedTime mins",
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("₹$finalTotal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (cartItems.isEmpty()) return@Button
                            isPlacingOrder = true

                            val newOrderId = "ORD-${System.currentTimeMillis().toString().takeLast(4)}"

                            val orderData = Order(
                                orderId = newOrderId,
                                studentName = "Student User",
                                itemName = cartItems.joinToString(", ") { it.name },
                                outletName = "Mingos",
                                price = finalTotal,
                                status = "PENDING",
                                timestamp = Date(),
                                predictedTime = predictedTime // SAVING THE PREDICTION
                            )

                            db.collection("orders").document(newOrderId).set(orderData)
                                .addOnSuccessListener {
                                    isPlacingOrder = false
                                    Toast.makeText(context, "Order Sent!", Toast.LENGTH_SHORT).show()
                                    onOrderSuccess(newOrderId)
                                }
                                .addOnFailureListener {
                                    isPlacingOrder = false
                                    Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isPlacingOrder && !isLoadingPrediction
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Confirm & Pay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize()
        ) {
            item {
                Text("Your Selection", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 16.dp))
            }

            items(cartItems) { item ->
                CartItemCard(item)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Bill details UI
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        BillRow("Subtotal", "₹$totalAmount")
                        BillRow("Tax (Platform Fee)", "₹$tax")
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        BillRow("Total", "₹$finalTotal", isBold = true)
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CartItemCard(item: FoodItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(item.color))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text("₹${item.price}", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
        }
        Text("x1", fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun BillRow(label: String, value: String, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = if (isBold) Color.Black else Color.Gray, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = if (isBold) Color.Black else Color.Gray, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}