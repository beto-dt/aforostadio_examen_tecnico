package com.example.aforoestadio.domain.engine

import com.example.aforoestadio.data.model.AssignmentStatus
import com.example.aforoestadio.data.model.EntryEvent
import com.example.aforoestadio.data.model.ProcessedEvent
import com.example.aforoestadio.data.repository.StadiumRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventProcessor(
    private val engine: AssignmentEngine,
    private val repository: StadiumRepository
) {
    private val mutex = Mutex()

    suspend fun process(event: EntryEvent): ProcessedEvent {
        return mutex.withLock {

            val currentState = repository.getState()

            val result = engine.processEvent(event, currentState)

            if (result.status == AssignmentStatus.ACCEPTED) {
                repository.assignAttendee(
                    sectorGate = result.assignedSector!!,
                    blockId = result.assignedBlock!!,
                    distance = result.distance!!
                )
            }

            repository.addProcessedEvent(result)

            result
        }
    }
}