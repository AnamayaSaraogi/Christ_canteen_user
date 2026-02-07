# 🍔 Christ Canteen - Student App (Smart Queue System)

> **Hackathon Project:** Solving campus dining congestion with Real-Time Queue Prediction and Live Order Tracking.

## 📱 Project Overview
The **Christ Canteen Student App** is designed to eliminate long waiting lines during break hours. Unlike traditional food delivery apps, this system focuses on **high-concurrency queue management**. It uses a custom heuristic algorithm to predict wait times based on kitchen load, item complexity, and historical rush patterns.

## ✨ Key Features

### 1.  AI Queue Prediction Engine
* **Real-time Logic:** Calculates wait time *before* payment based on the number of active orders in the kitchen.
* **Adaptive Algorithms:** Adjusts estimates dynamically during "Rush Hours" (12:30 PM - 1:30 PM) using time-weighted multipliers.
* **Transparency:** Shows students exactly how many people are ahead of them in the virtual line.

### 2.  Live Order Tracking (Real-Time)
* **Instant Updates:** Powered by **Firebase Firestore Listeners**, status changes (Pending → Preparing → Ready) reflect instantly without refreshing.
* **Dynamic Countdown:** The "Estimated Time" counts down automatically as other students pick up their orders.
* **Smart Notifications:** Alerts students when their order enters preparation or is ready for pickup.

### 3.  Congestion Control (Late Pickup Protocol)
* **The "Penalty Box" Logic:** If an order remains "Ready" for >10 minutes, the status screen turns **RED**.
* **Flow Management:** Instructs students to collect from a secondary "Holding Area" to keep the main counter clear for new orders.

### 4.  Seamless Ordering
* **Multi-Outlet Support:** Scalable architecture to support multiple vendors (Mingos, Nandini, Fresh Juice, etc.).
* **Cart & Checkout:** Simple, distraction-free UI for quick order placement during short breaks.

---

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material3 Design)
* **Backend:** Firebase Firestore (NoSQL Real-time Database)
* **Architecture:** MVVM (Model-View-ViewModel) concept
* **Algorithm:** Custom Heuristic Prediction Engine

---

## 🔮 Future Roadmap
* [ ] **Machine Learning Integration:** Replace heuristic multipliers with a TensorFlow Lite model trained on actual semester data.
* [ ] **Group Ordering:** Allow friends to pool orders into a single pickup token.
* [ ] **Dietary Filters:** AI-based recommendations for Vegan/Gluten-free options.

