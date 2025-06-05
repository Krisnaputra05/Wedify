package com.example.wedify.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalLine(
    color: Color = Color.LightGray,
    thickness: Dp = 2.dp,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
) {
    Canvas(
        modifier = modifier
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(x = 0f, y = size.height / 2),
            end = Offset(x = size.width, y = size.height / 2),
            strokeWidth = thickness.toPx(),
            cap = StrokeCap.Round
        )
    }
}