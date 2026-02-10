package com.example.aforoestadio.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.aforoestadio.data.model.Sector

@Composable
fun SectorCard(
    sector: Sector,
    modifier: Modifier = Modifier
) {
    val occupancyPercent by animateFloatAsState(
        targetValue = if (sector.totalCapacity > 0) {
            sector.totalOccupancy.toFloat() / sector.totalCapacity.toFloat()
        } else 0f,
        label = "sector_occupancy_${sector.name}"
    )

    val barColor = when {
        occupancyPercent >= 0.70f -> Color.Red
        occupancyPercent >= 0.50f -> Color(0xFFFFA000) // naranja
        else -> Color(0xFF4CAF50) // verde
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sector.name.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${sector.totalOccupancy}/${sector.totalCapacity}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { occupancyPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                sector.blocks.values
                    .sortedBy { it.id.ordinal }
                    .forEach { block ->
                        BlockIndicator(
                            block = block,
                            modifier = Modifier.weight(1f)
                        )
                    }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dist. media: ${sector.averageDistance.toInt()}m",
                fontSize = 9.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun SectorPair(
    left: Sector,
    right: Sector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectorCard(
            sector = left,
            modifier = Modifier.weight(1f)
        )
        SectorCard(
            sector = right,
            modifier = Modifier.weight(1f)
        )
    }
}