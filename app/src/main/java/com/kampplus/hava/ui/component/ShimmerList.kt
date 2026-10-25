package com.kampplus.hava.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.ui.theme.HavaTheme

@Composable
fun ShimmerList(modifier: Modifier = Modifier, itemCount: Int = 3) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(itemCount) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(brush, CircleShape)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.72f)
                                .height(18.dp)
                                .background(brush, RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.44f)
                                .height(14.dp)
                                .background(brush, RoundedCornerShape(12.dp))
                        )
                    }
                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            Modifier
                                .size(width = 50.dp, height = 18.dp)
                                .background(brush, RoundedCornerShape(12.dp))
                        )
                        Box(
                            Modifier
                                .size(width = 32.dp, height = 12.dp)
                                .background(brush, RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val translate by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = SHIMMER_DISTANCE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.055f),
            Color.White.copy(alpha = 0.13f),
            Color.White.copy(alpha = 0.055f)
        ),
        start = Offset(translate - SHIMMER_WIDTH, 0f),
        end = Offset(translate, 0f)
    )
}

private const val SHIMMER_DISTANCE = 1_500f
private const val SHIMMER_WIDTH = 400f

@Preview(showBackground = true)
@Composable
private fun ShimmerListPreview() {
    HavaTheme {
        ShimmerList()
    }
}
