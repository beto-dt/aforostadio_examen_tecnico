package com.example.aforoestadio.data.model

import java.util.concurrent.atomic.AtomicLong

data class ProcessedEvent(
    val event: EntryEvent,
    val status: AssignmentStatus,
    val assignedSector: Gate? = null,
    val assignedBlock: BlockId? = null,
    val distance: Int? = null,
    val reason: String? = null,
    val eventId: String = "event-${event.timestamp}-${idCounter.incrementAndGet()}"
) {
    companion object {
        private val idCounter = AtomicLong(0)
    }
}