package com.example.christ_canteen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStatusScreen(orderId: String, onHomeClick: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // --- STATE VARIABLES ---
    var status by remember { mutableStateOf("Fetching...") }
    var orderTimestamp by remember { mutableStateOf<Timestamp?>(null) }
    var readyTimestamp by remember { mutableStateOf<Date?>(null) } // Track WHEN it became ready

    // LIVE QUEUE METRICS
    var peopleAhead by remember { mutableStateOf(0) }
    var estimatedTime by remember { mutableStateOf(15) }
    var tokenNumber by remember { mutableStateOf("...") }

    // LATE PICKUP LOGIC
    var isLate by remember { mutableStateOf(false) }
    var timeSinceReady by remember { mutableStateOf("0 mins") }

    // NOTIFICATION PERMISSION
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // --- LISTENER: WATCH ORDER & CHECK FOR LATENESS ---
    LaunchedEffect(orderId) {
        if (orderId.isNotEmpty()) {
            while(true) { // Loop to update "Time Since Ready" every minute
                db.collection("orders").document(orderId).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot != null && snapshot.exists()) {
                            val order = snapshot.toObject(Order::class.java)
                            status = order?.status ?: "Unknown"
                            tokenNumber = "#" + (order?.orderId?.takeLast(4) ?: "00")

                            // 1. CHECK IF LATE
                            // In a real app, you'd save a "readyTime" field in Firebase.
                            // Here we simulate it: If status is READY, we assume it's been ready for X mins
                            // based on local time for the demo.

                            if (status == "READY") {
                                // For Demo: We just check if it's been ready for > 10 mins
                                // You would normally compare Timestamp.now() - readyTimestamp
                                isLate = false // Toggle this to true to test the UI
                            }
                        }
                    }
                delay(60000) // Check every 1 min
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Live Tracking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onHomeClick) { Icon(Icons.Default.Home, "Home") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(if (isLate) Color(0xFFFFEBEE) else Color(0xFFF9F9F9)) // Red tint if late
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. STATUS HEADER
            Text("STATUS", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

            if (isLate) {
                // LATE UI
                Text(
                    text = "LATE PICKUP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F) // Red
                )
                Text("Moved to Holding Area", fontSize = 14.sp, color = Color.Red)
            } else {
                // NORMAL UI
                Text(
                    text = when (status) {
                        "PENDING" -> "Waiting in Queue"
                        "PREPARING" -> "Cooking Now"
                        "READY" -> "Ready for Pickup!"
                        else -> status
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (status == "READY") Color(0xFF4CAF50) else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. TOKEN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TOKEN NUMBER", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(tokenNumber, fontSize = 64.sp, fontWeight = FontWeight.Black)

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLate) {
                        // LATE WARNING
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Please collect from Shelf B", color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (status == "PENDING") {
                        // QUEUE INFO
                        Text("People ahead: $peopleAhead", color = Color.Blue)
                    } else if (status == "READY") {
                        Text("Counter: 2", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(isLate) Color.Red else Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if(isLate) "I Have Collected It" else "Order More")
            }
        }
    }
}