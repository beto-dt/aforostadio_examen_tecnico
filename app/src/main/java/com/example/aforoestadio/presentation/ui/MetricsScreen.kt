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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.aforoestadio.data.model.Sector
import com.example.aforoestadio.data.model.StadiumState
import com.example.aforoestadio.presentation.components.KpiCard

@Composable
fun MetricsScreen(
    state: StadiumState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Métricas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            KpiCard(
                title = "Total Procesados",
                value = "${state.totalProcessed}",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            KpiCard(
                title = "Asignados",
                value = "${state.totalAssigned}",
                modifier = Modifier.weight(1f),
                valueColor = Color(0xFF4CAF50)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            KpiCard(
                title = "Bloqueados",
                value = "${state.totalBlocked}",
                modifier = Modifier.weight(1f),
                valueColor = Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            KpiCard(
                title = "Dist. Promedio",
                value = "${state.globalAverageDistance.toInt()}m",
                modifier = Modifier.weight(1f),
                valueColor = Color(0xFFFFA000)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ocupación por Sector",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        state.sectors.forEach { (gate, sector) ->
            SectorOccupancyBar(gate = gate, sector = sector)
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Distancia Media por Sector",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        state.sectors.forEach { (gate, sector) ->
            SectorDistanceBar(gate = gate, sector = sector)
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Detalle por Bloque",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        state.sectors.forEach { (gate, sector) ->
            SectorBlockDetail(gate = gate, sector = sector)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun SectorOccupancyBar(
    gate: Gate,
    sector: Sector,
    modifier: Modifier = Modifier
) {
    val percent = if (sector.totalCapacity > 0) {
        sector.totalOccupancy.toFloat() / sector.totalCapacity.toFloat()
    } else 0f

    val animatedPercent by animateFloatAsState(
        targetValue = percent,
        label = "occupancy_${gate.name}"
    )

    val barColor = when {
        animatedPercent >= 0.70f -> Color.Red
        animatedPercent >= 0.50f -> Color(0xFFFFA000)
        else -> Color(0xFF4CAF50)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = gate.name,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            modifier = Modifier.width(60.dp)
        )

        LinearProgressIndicator(
            progress = { animatedPercent },
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = "${sector.totalOccupancy}/${sector.totalCapacity}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = barColor,
            modifier = Modifier
                .width(55.dp)
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun SectorDistanceBar(
    gate: Gate,
    sector: Sector,
    modifier: Modifier = Modifier
) {
    val maxDistance = 250f
    val avgDist = sector.averageDistance.toFloat()

    val animatedPercent by animateFloatAsState(
        targetValue = (avgDist / maxDistance).coerceIn(0f, 1f),
        label = "distance_${gate.name}"
    )

    val barColor = Color(0xFF42A5F5)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = gate.name,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            modifier = Modifier.width(60.dp)
        )

        LinearProgressIndicator(
            progress = { animatedPercent },
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = "${avgDist.toInt()}m",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = barColor,
            modifier = Modifier
                .width(55.dp)
                .padding(start = 8.dp)
        )
    }
}

@Composable
private fun SectorBlockDetail(
    gate: Gate,
    sector: Sector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Sector ${gate.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableHeader("Bloque", Modifier.weight(1f))
                TableHeader("Ocup.", Modifier.weight(1f))
                TableHeader("Estado", Modifier.weight(1f))
                TableHeader("Dist.", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(4.dp))

            sector.blocks.values
                .sortedBy { it.id.ordinal }
                .forEach { block ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.id.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${block.currentOccupancy}/${block.capacity}",
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = block.status.name,
                            fontSize = 11.sp,
                            color = if (block.isAvailable) {
                                Color(0xFF4CAF50)
                            } else {
                                Color.Red
                            },
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${block.averageDistance.toInt()}m",
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
        }
    }
}

@Composable
private fun TableHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = modifier
    )
}