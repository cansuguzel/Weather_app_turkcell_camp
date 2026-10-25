package com.kampplus.hava.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kampplus.hava.ui.theme.GlassBorder
import com.kampplus.hava.ui.theme.GlassFill
import com.kampplus.hava.ui.theme.MainText

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    color: Color = GlassFill,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.border(1.dp, GlassBorder, shape),
        shape = shape,
        color = color,
        contentColor = MainText,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        content = content
    )
}
