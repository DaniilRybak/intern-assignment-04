package com.example.intern_assignment_04.feature.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TimeDisplay(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 56.sp,
    color: Color = Color.Black,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.displayLarge,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        color = color,
    )
}

@Composable
fun SplitTimeDisplay(
    mainText: String,
    secondaryText: String,
    modifier: Modifier = Modifier,
    mainFontSize: TextUnit = 56.sp,
    secondaryFontSize: TextUnit = 24.sp,
    color: Color = Color.Black,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimeDisplay(
            text = mainText,
            fontSize = mainFontSize,
            color = color,
        )
        Text(
            text = secondaryText,
            style = MaterialTheme.typography.titleLarge,
            fontSize = secondaryFontSize,
            textAlign = TextAlign.Center,
            color = color,
        )
    }
}

