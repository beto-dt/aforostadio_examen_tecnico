package com.example.aforoestadio.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aforoestadio.data.model.Gate
import com.example.aforoestadio.data.model.StadiumState
import com.example.aforoestadio.data.websocket.ConnectionState
import com.example.aforoestadio.presentation.components.ConnectionIndicator
import com.example.aforoestadio.presentation.components.SectorCard
import com.example.aforoestadio.presentation.components.SectorPair

@Composable
fun MapScreen(
    state: StadiumState,
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ConnectionIndicator(state = connectionState)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mapa del Estadio",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        GlobalCapacityBar(state = state)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(label = "Procesados", value = "${state.totalProcessed}")
            StatItem(label = "Asignados", value = "${state.totalAssigned}")
            StatItem(label = "Bloqueados", value = "${state.totalBlocked}")
            StatItem(
                label = "Dist. media",
                value = "${state.globalAverageDistance.toInt()}m"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Norte - Sur",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        SectorPair(
            left = state.sectors[Gate.NORTE]!!,
            right = state.sectors[Gate.SUR]!!
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Este - Oeste",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        SectorPair(
            left = state.sectors[Gate.ESTE]!!,
            right = state.sectors[Gate.OESTE]!!
        )
    }
}

@Composable
private fun GlobalCapacityBar(
    state: StadiumState,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = state.occupancyPercentage,
        label = "global_capacity"
    )

    val barColor = when {
        progress >= 0.70f -> Color.Red
        progress >= 0.50f -> Color(0xFFFFA000)
        else -> Color(0xFF4CAF50)
    }

    val percentage = (progress * 100).toInt()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Capacidad Global",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${state.totalOccupancy} / ${state.totalCapacity}  ($percentage%)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = barColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}