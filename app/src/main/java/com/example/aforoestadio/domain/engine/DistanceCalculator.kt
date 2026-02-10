package com.example.aforoestadio.domain.engine

import com.example.aforoestadio.data.model.BlockId
import com.example.aforoestadio.data.model.Gate

object DistanceCalculator {

    private val SECTOR_DISTANCES: Map<Pair<Gate, Gate>, Int> = mapOf(
        (Gate.NORTE to Gate.NORTE) to 0,
        (Gate.NORTE to Gate.SUR) to 200,
        (Gate.NORTE to Gate.ESTE) to 100,
        (Gate.NORTE to Gate.OESTE) to 100,

        (Gate.SUR to Gate.NORTE) to 200,
        (Gate.SUR to Gate.SUR) to 0,
        (Gate.SUR to Gate.ESTE) to 100,
        (Gate.SUR to Gate.OESTE) to 100,

        (Gate.ESTE to Gate.NORTE) to 100,
        (Gate.ESTE to Gate.SUR) to 100,
        (Gate.ESTE to Gate.ESTE) to 0,
        (Gate.ESTE to Gate.OESTE) to 200,

        (Gate.OESTE to Gate.NORTE) to 100,
        (Gate.OESTE to Gate.SUR) to 100,
        (Gate.OESTE to Gate.ESTE) to 200,
        (Gate.OESTE to Gate.OESTE) to 0
    )


    private val BLOCK_DISTANCES: Map<BlockId, Int> = mapOf(
        BlockId.C to 10,
        BlockId.B to 30,
        BlockId.A to 50
    )

    fun calculate(gate: Gate, targetSector: Gate, targetBlock: BlockId): Int {
        val sectorDistance = SECTOR_DISTANCES[gate to targetSector] ?: 0
        val blockDistance = BLOCK_DISTANCES[targetBlock] ?: 0
        return sectorDistance + blockDistance
    }

    fun getSectorsByProximity(gate: Gate): List<Gate> =
        Gate.entries.sortedBy { targetSector ->
            SECTOR_DISTANCES[gate to targetSector] ?: Int.MAX_VALUE
        }
}