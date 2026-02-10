package com.example.aforoestadio.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aforoestadio.data.model.StadiumState
import com.example.aforoestadio.presentation.components.EventCard
import com.example.aforoestadio.presentation.components.KpiCard

@Composable
fun LogScreen(
    state: StadiumState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Log de Eventos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            KpiCard(
                title = "Aceptados",
                value = "${state.totalAssigned}",
                modifier = Modifier.weight(1f),
                valueColor = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.width(8.dp))
            KpiCard(
                title = "Bloqueados",
                value = "${state.totalBlocked}",
                modifier = Modifier.weight(1f),
                valueColor = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Feed de Eventos",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${state.eventLog.size} eventos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.eventLog.isEmpty()) {
            Text(
                text = "Esperando eventos del WebSocket...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = state.eventLog,
                    key = { it.eventId }
                ) { processedEvent ->
                    EventCard(event = processedEvent)
                }
            }
        }
    }
}