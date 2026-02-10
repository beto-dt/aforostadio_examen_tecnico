package com.example.aforoestadio.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aforoestadio.data.websocket.ConnectionState

@Composable
fun ConnectionIndicator(
    state: ConnectionState,
    modifier: Modifier = Modifier
) {
    val (baseColor, text) = when (state) {
        is ConnectionState.Disconnected ->
            Color.Red to "Desconectado"
        is ConnectionState.Reconnecting ->
            Color(0xFFFFA000) to "Reconectando..."
        is ConnectionState.ConnectedIdle ->
            Color(0xFF4CAF50) to "Conectado"
        is ConnectionState.ConnectedWorking ->
            Color(0xFF4CAF50) to "Procesando"
    }

    val shouldPulse = state is ConnectionState.Reconnecting ||
            state is ConnectionState.ConnectedWorking

    val alpha: Float = if (shouldPulse) {
        val infiniteTransition = rememberInfiniteTransition(
            label = "connection_pulse"
        )
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
        animatedAlpha
    } else {
        1f
    }

    val animatedColor by animateColorAsState(
        targetValue = baseColor,
        animationSpec = tween(durationMillis = 300),
        label = "connection_color"
    )

    val backgroundColor = when (state) {
        is ConnectionState.Disconnected ->
            Color.Red.copy(alpha = 0.1f)
        is ConnectionState.Reconnecting ->
            Color(0xFFFFA000).copy(alpha = 0.1f)
        else ->
            Color(0xFF4CAF50).copy(alpha = 0.1f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(animatedColor)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = animatedColor
        )
    }
}