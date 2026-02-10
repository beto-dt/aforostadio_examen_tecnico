package com.example.aforoestadio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aforoestadio.data.model.Block
import com.example.aforoestadio.data.model.BlockStatus

@Composable
fun BlockIndicator(
    block: Block,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Bloque ${block.id}",
                tint = when {
                    block.status == BlockStatus.BLOCKED -> Color.Red
                    block.currentOccupancy > 0 -> MaterialTheme.colorScheme.primary
                    else -> Color.Gray
                },
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = block.id.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${block.currentOccupancy}/${block.capacity}",
                fontSize = 8.sp,
                color = Color.Gray
            )
        }

        if (block.status == BlockStatus.BLOCKED) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Red.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "70%\nBLOQUEADO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp
                )
            }
        }
    }
}