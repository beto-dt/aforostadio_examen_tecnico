package com.example.aforoestadio.data.model

data class StadiumState(
    val sectors: Map<Gate, Sector>,
    val totalProcessed: Int = 0,
    val totalAssigned: Int = 0,
    val totalBlocked: Int = 0,
    val eventLog: List<ProcessedEvent> = emptyList()
) {

    val totalCapacity: Int
        get() = sectors.values.sumOf { it.totalCapacity }


    val totalOccupancy: Int
        get() = sectors.values.sumOf { it.totalOccupancy }


    val globalAverageDistance: Double
        get() {
            val allDistances = sectors.values.flatMap { sector ->
                sector.blocks.values.flatMap { it.distances }
            }
            return if (allDistances.isNotEmpty()) allDistances.average() else 0.0
        }
    
    val occupancyPercentage: Float
        get() = if (totalCapacity > 0) {
            totalOccupancy.toFloat() / totalCapacity.toFloat()
        } else 0f
}