package com.example.christ_canteen

import java.util.Calendar

object PredictionEngine {

    // 1. BASE PREP TIME (Average time per item)
    private const val AVERAGE_PREP_TIME = 2 // mins

    /**
     * Calculates the estimated wait time.
     * @param queueLength: How many people are ahead in the queue.
     * @param itemComplexity: Extra time for complex items (default 5 mins).
     */
    fun calculateWaitTime(queueLength: Int, itemComplexity: Int = 5): Int {

        // FACTOR 1: Queue Delay
        // We assume the kitchen processes 2 orders in parallel
        val kitchenCapacity = 2
        val batchesAhead = queueLength / kitchenCapacity
        val queueDelay = batchesAhead * AVERAGE_PREP_TIME

        // FACTOR 2: Rush Hour Multiplier
        val rushMultiplier = getRushMultiplier()

        // CORE FORMULA: (Queue Wait + Prep Time) * Rush Factor
        val totalTime = (queueDelay + itemComplexity) * rushMultiplier

        return totalTime.toInt()
    }

    // FACTOR 3: Time-based Rush Intensity
    private fun getRushMultiplier(): Double {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // LUNCH RUSH (12:30 PM - 1:30 PM) -> 1.5x Slower
        if (hour == 12 && minute >= 30 || hour == 13 && minute <= 30) {
            return 1.5
        }

        // SHORT BREAK (10:30 AM - 11:00 AM) -> 1.3x Slower
        if (hour == 10 && minute >= 30) {
            return 1.3
        }

        return 1.0 // Normal Speed
    }
}