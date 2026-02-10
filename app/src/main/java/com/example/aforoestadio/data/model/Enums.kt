package com.example.aforoestadio.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Gate { NORTE, SUR, ESTE, OESTE }

@Serializable
enum class ShirtColor { BLUE, MULTICOLOR, RED, GREEN, BLACK, WHITE }

enum class BlockId { C, B, A }

enum class BlockStatus { OPEN, BLOCKED }

enum class AssignmentStatus { ACCEPTED, REJECTED }