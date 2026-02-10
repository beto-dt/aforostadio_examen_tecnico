package com.example.aforoestadio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aforoestadio.data.model.ShirtColor

@Composable
fun ShirtColorBadge(
    shirtColor: ShirtColor,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (shirtColor) {
        ShirtColor.BLUE -> Triple(
            Color(0xFF1976D2), Color.White, "BLUE"
        )
        ShirtColor.MULTICOLOR -> Triple(
            Color(0xFF9C27B0), Color.White, "MULTI"
        )
        ShirtColor.RED -> Triple(
            Color(0xFFD32F2F), Color.White, "RED"
        )
        ShirtColor.GREEN -> Triple(
            Color(0xFF388E3C), Color.White, "GREEN"
        )
        ShirtColor.BLACK -> Triple(
            Color(0xFF212121), Color.White, "BLACK"
        )
        ShirtColor.WHITE -> Triple(
            Color(0xFFEEEEEE), Color.Black, "WHITE"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}