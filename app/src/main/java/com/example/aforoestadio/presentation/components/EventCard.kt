package com.example.aforoestadio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aforoestadio.data.model.AssignmentStatus
import com.example.aforoestadio.data.model.ProcessedEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventCard(
    event: ProcessedEvent,
    modifier: Modifier = Modifier
) {
    val isAccepted = event.status == AssignmentStatus.ACCEPTED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccepted) {
                Color(0xFFF1F8E9)
            } else {
                Color(0xFFFDE8E8)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isAccepted) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Cancel
                },
                contentDescription = if (isAccepted) "Aceptado" else "Rechazado",
                tint = if (isAccepted) Color(0xFF4CAF50) else Color.Red,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = event.eventId,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ShirtColorBadge(event.event.shirtColor)
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (isAccepted) {
                    Text(
                        text = "Sector: ${event.assignedSector?.name} | " +
                                "Bloque: ${event.assignedBlock?.name} | " +
                                "Dist: ${event.distance}m",
                        fontSize = 11.sp,
                        color = Color(0xFF388E3C)
                    )
                } else {
                    Text(
                        text = event.reason ?: "Acceso bloqueado",
                        fontSize = 11.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Puerta ${event.event.gate.name}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTimestamp(event.event.timestamp),
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.format(Date(timestamp * 1000))
    } catch (e: Exception) {
        "--:--:--"
    }
}