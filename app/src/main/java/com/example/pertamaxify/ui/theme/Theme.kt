package com.example.pertamaxify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun PertamaxifyTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        background = BackgroundColor,
        surface = BackgroundColor
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
