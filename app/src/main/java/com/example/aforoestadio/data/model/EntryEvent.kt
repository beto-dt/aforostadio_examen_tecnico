package com.example.aforoestadio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EntryEvent(
    val type: String,
    val timestamp: Long,
    val gate: Gate,
    val shirtColor: ShirtColor
)