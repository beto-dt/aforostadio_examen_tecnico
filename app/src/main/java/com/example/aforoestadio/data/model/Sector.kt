package com.example.aforoestadio.data.model

data class Sector(
    val name: Gate,
    val blocks: Map<BlockId, Block>
) {

    val totalOccupancy: Int
        get() = blocks.values.sumOf { it.currentOccupancy }

    val totalCapacity: Int
        get() = blocks.values.sumOf { it.capacity }

    val hasAvailableBlocks: Boolean
        get() = blocks.values.any { it.isAvailable }

    val averageDistance: Double
        get() {
            val allDistances = blocks.values.flatMap { it.distances }
            return if (allDistances.isNotEmpty()) allDistances.average() else 0.0
        }
    
    fun getAvailableBlocks(): List<Block> =
        blocks.values
            .filter { it.isAvailable }
            .sortedBy { it.id.ordinal }
}