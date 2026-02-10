package com.example.aforoestadio.data.model

data class Block(
    val id: BlockId,
    val sectorName: Gate,
    val capacity: Int,
    val currentOccupancy: Int = 0,
    val status: BlockStatus = BlockStatus.OPEN,
    val distances: List<Int> = emptyList()
) {

    val isAvailable: Boolean
        get() = status == BlockStatus.OPEN && currentOccupancy < capacity


    val averageDistance: Double
        get() = if (distances.isNotEmpty()) distances.average() else 0.0


    fun addAttendee(distance: Int): Block {
        val newOccupancy = currentOccupancy + 1
        val newDistances = distances + distance
        val newStatus = if (newOccupancy.toDouble() / capacity >= 0.70) {
            BlockStatus.BLOCKED
        } else {
            status
        }
        return copy(
            currentOccupancy = newOccupancy,
            distances = newDistances,
            status = newStatus
        )
    }
}