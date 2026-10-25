package com.kampplus.hava.ui.component

import android.animation.ValueAnimator
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kampplus.hava.ui.theme.AccentYellow

@Composable
fun SunOrb(orbSize: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var animationsEnabled by remember(context, owner) {
        mutableStateOf(ValueAnimator.areAnimatorsEnabled() && owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
    }
    DisposableEffect(context, owner) {
        val lifecycleObserver = LifecycleEventObserver { _, _ ->
            animationsEnabled =
                ValueAnimator.areAnimatorsEnabled() && owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }
        val settingObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                animationsEnabled =
                    ValueAnimator.areAnimatorsEnabled() && owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            }
        }
        owner.lifecycle.addObserver(lifecycleObserver)
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            settingObserver
        )
        onDispose {
            owner.lifecycle.removeObserver(lifecycleObserver)
            context.contentResolver.unregisterContentObserver(settingObserver)
        }
    }
    val transition = rememberInfiniteTransition(label = "sunHalo")
    val rotation = if (animationsEnabled) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(SUN_ROTATION_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "sunRayRotation"
        ).value
    } else {
        0f
    }
    Canvas(modifier = modifier.size(orbSize)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.29f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x55FFC900), Color(0x00FFC900)),
                center = center,
                radius = size.minDimension * 0.49f
            ),
            radius = size.minDimension * 0.49f,
            center = center
        )
        for (index in 0 until SUN_RAYS) {
            val angle = index * (Math.PI * 2.0 / SUN_RAYS) + Math.toRadians(rotation.toDouble())
            val direction = Offset(kotlin.math.cos(angle).toFloat(), kotlin.math.sin(angle).toFloat())
            drawLine(
                color = AccentYellow.copy(alpha = 0.78f),
                start = center + direction * (radius * 1.28f),
                end = center + direction * (radius * 1.53f),
                strokeWidth = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF2B0), AccentYellow, Color(0xFFFFA83A)),
                center = Offset(center.x - radius * 0.35f, center.y - radius * 0.42f),
                radius = radius * 1.8f
            ),
            radius = radius,
            center = center
        )
        drawCircle(
            color = Color(0xFFFFF2B0).copy(alpha = 0.5f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private const val SUN_RAYS = 8
private const val SUN_ROTATION_MS = 60_000

@Preview(showBackground = true)
@Composable
private fun SunOrbPreview() {
    SunOrb(orbSize = 120.dp)
}
