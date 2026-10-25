package com.kampplus.hava.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

@Composable
fun MoonOrb(phase: Float, orbSize: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(orbSize)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = this.size.minDimension * 0.43f
        val litRadius = radius * (1f - MOON_INSET)
        val phaseValue = phase.coerceIn(0f, 1f)
        val angle = (2.0 * PI * phaseValue).toFloat()
        val waxing = phaseValue <= 0.5f
        val terminatorScale = cos(angle)
        val moonBoundary = Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    center.x - litRadius,
                    center.y - litRadius,
                    center.x + litRadius,
                    center.y + litRadius
                )
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x66FFE6A1), Color(0x00FFE6A1)),
                center = center,
                radius = radius * 1.85f
            ),
            radius = radius * 1.85f,
            center = center
        )
        drawCircle(Color(0xFF27203F), radius, center)

        val illumination = Path()
        val steps = 40
        val outerSide = if (waxing) 1f else -1f
        for (step in 0..steps) {
            val y = -litRadius + (2f * litRadius * step / steps)
            val outerX = outerSide * sqrt((litRadius * litRadius - y * y).coerceAtLeast(0f))
            val point = Offset(center.x + outerX, center.y + y)
            if (step == 0) illumination.moveTo(point.x, point.y) else illumination.lineTo(point.x, point.y)
        }
        for (step in steps downTo 0) {
            val y = -litRadius + (2f * litRadius * step / steps)
            val edgeX = sqrt((litRadius * litRadius - y * y).coerceAtLeast(0f))
            val innerX = if (waxing) terminatorScale * edgeX else -terminatorScale * edgeX
            illumination.lineTo(center.x + innerX, center.y + y)
        }
        illumination.close()

        clipPath(moonBoundary) {
            drawPath(
                path = illumination,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF3CB), Color(0xFFE7D18F), Color(0xFFC0A963)),
                    center = Offset(center.x - radius * 0.25f, center.y - radius * 0.35f),
                    radius = radius * 1.8f
                )
            )
            listOf(
                Triple(-0.34f, -0.22f, 0.13f),
                Triple(0.18f, -0.05f, 0.19f),
                Triple(-0.10f, 0.31f, 0.12f),
                Triple(0.32f, 0.28f, 0.10f)
            ).forEach { (x, y, craterRadius) ->
                drawCircle(
                    color = Color(0xFF8C7548).copy(alpha = 0.17f),
                    radius = radius * craterRadius,
                    center = Offset(center.x + radius * x, center.y + radius * y)
                )
            }
        }
        drawCircle(
            color = Color(0xFFFFF1C8).copy(alpha = 0.55f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private const val MOON_INSET = 0.07f
