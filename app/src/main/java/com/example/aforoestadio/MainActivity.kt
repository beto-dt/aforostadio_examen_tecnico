package com.example.aforoestadio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aforoestadio.presentation.navigation.MainScreen
import com.example.aforoestadio.ui.theme.AforoEstadioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AforoEstadioTheme {
                MainScreen()
            }
        }
    }
}