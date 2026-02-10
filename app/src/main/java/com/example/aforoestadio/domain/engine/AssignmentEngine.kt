package com.example.aforoestadio.domain.engine

import com.example.aforoestadio.data.model.*

class AssignmentEngine {

    fun processEvent(
        event: EntryEvent,
        state: StadiumState
    ): ProcessedEvent {

        if (event.shirtColor == ShirtColor.MULTICOLOR) {
            return ProcessedEvent(
                event = event,
                status = AssignmentStatus.REJECTED,
                reason = "Acceso bloqueado"
            )
        }

        if (event.shirtColor == ShirtColor.BLUE) {
            return processBlueShirt(event, state)
        }

        return processStandard(event, state)
    }

    private fun processBlueShirt(
        event: EntryEvent,
        state: StadiumState
    ): ProcessedEvent {

        val sectorNorte = state.sectors[Gate.NORTE]

        if (sectorNorte != null && sectorNorte.hasAvailableBlocks) {
            val block = sectorNorte.getAvailableBlocks().first()
            val distance = DistanceCalculator.calculate(
                gate = event.gate,
                targetSector = Gate.NORTE,
                targetBlock = block.id
            )
            return ProcessedEvent(
                event = event,
                status = AssignmentStatus.ACCEPTED,
                assignedSector = Gate.NORTE,
                assignedBlock = block.id,
                distance = distance
            )
        }

        val sectorsOrdered = DistanceCalculator.getSectorsByProximity(event.gate)

        for (sectorGate in sectorsOrdered) {
            val sector = state.sectors[sectorGate] ?: continue
            val blockC = sector.blocks[BlockId.C]

            if (blockC != null && blockC.isAvailable) {
                val distance = DistanceCalculator.calculate(
                    gate = event.gate,
                    targetSector = sectorGate,
                    targetBlock = BlockId.C
                )
                return ProcessedEvent(
                    event = event,
                    status = AssignmentStatus.ACCEPTED,
                    assignedSector = sectorGate,
                    assignedBlock = BlockId.C,
                    distance = distance
                )
            }
        }

        return ProcessedEvent(
            event = event,
            status = AssignmentStatus.REJECTED,
            reason = "Camiseta azul sin Bloque C disponible"
        )
    }

    private fun processStandard(
        event: EntryEvent,
        state: StadiumState
    ): ProcessedEvent {

        val sectorsOrdered = DistanceCalculator.getSectorsByProximity(event.gate)

        for (sectorGate in sectorsOrdered) {
            val sector = state.sectors[sectorGate] ?: continue
            val availableBlocks = sector.getAvailableBlocks()

            if (availableBlocks.isNotEmpty()) {
                val block = availableBlocks.first()
                val distance = DistanceCalculator.calculate(
                    gate = event.gate,
                    targetSector = sectorGate,
                    targetBlock = block.id
                )
                return ProcessedEvent(
                    event = event,
                    status = AssignmentStatus.ACCEPTED,
                    assignedSector = sectorGate,
                    assignedBlock = block.id,
                    distance = distance
                )
            }
        }

        return ProcessedEvent(
            event = event,
            status = AssignmentStatus.REJECTED,
            reason = "Estadio lleno - sin bloques disponibles"
        )
    }
}