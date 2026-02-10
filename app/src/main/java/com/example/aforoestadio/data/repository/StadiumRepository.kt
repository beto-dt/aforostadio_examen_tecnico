package com.example.aforoestadio.data.repository

import com.example.aforoestadio.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StadiumRepository {

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<StadiumState> = _state.asStateFlow()

    fun getState(): StadiumState = _state.value

    fun assignAttendee(sectorGate: Gate, blockId: BlockId, distance: Int) {
        _state.update { current ->
            val sector = current.sectors[sectorGate] ?: return@update current
            val block = sector.blocks[blockId] ?: return@update current

            val updatedBlock = block.addAttendee(distance)
            val updatedBlocks = sector.blocks + (blockId to updatedBlock)
            val updatedSector = sector.copy(blocks = updatedBlocks)
            val updatedSectors = current.sectors + (sectorGate to updatedSector)

            current.copy(
                sectors = updatedSectors,
                totalProcessed = current.totalProcessed + 1,
                totalAssigned = current.totalAssigned + 1
            )
        }
    }

    fun addProcessedEvent(event: ProcessedEvent) {
        _state.update { current ->
            val newLog = listOf(event) + current.eventLog.take(499)
            val isBlocked = event.status == AssignmentStatus.REJECTED

            current.copy(
                eventLog = newLog,
                totalProcessed = if (isBlocked) {
                    current.totalProcessed + 1
                } else {
                    current.totalProcessed
                },
                totalBlocked = if (isBlocked) {
                    current.totalBlocked + 1
                } else {
                    current.totalBlocked
                }
            )
        }
    }

    companion object {
        fun createInitialState(): StadiumState {
            val sectors = Gate.entries.associateWith { gate ->
                Sector(
                    name = gate,
                    blocks = mapOf(
                        BlockId.C to Block(
                            id = BlockId.C,
                            sectorName = gate,
                            capacity = 50
                        ),
                        BlockId.B to Block(
                            id = BlockId.B,
                            sectorName = gate,
                            capacity = 30
                        ),
                        BlockId.A to Block(
                            id = BlockId.A,
                            sectorName = gate,
                            capacity = 20
                        )
                    )
                )
            }
            return StadiumState(sectors = sectors)
        }
    }
}