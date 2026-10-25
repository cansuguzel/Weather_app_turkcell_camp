package com.kampplus.hava.ui.component

import android.animation.ValueAnimator
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kampplus.hava.ui.theme.DaySkyBottom
import com.kampplus.hava.ui.theme.DaySkyMid
import com.kampplus.hava.ui.theme.DaySkyTop
import com.kampplus.hava.ui.theme.MainText
import com.kampplus.hava.ui.theme.NightBackground
import com.kampplus.hava.ui.theme.NightBottom
import com.kampplus.hava.ui.theme.NightMid
import com.kampplus.hava.ui.theme.RainBottom
import com.kampplus.hava.ui.theme.RainMid
import com.kampplus.hava.ui.theme.RainTop
import com.kampplus.hava.ui.theme.SnowBottom
import com.kampplus.hava.ui.theme.SnowMid
import com.kampplus.hava.ui.theme.SnowTop
import com.kampplus.hava.ui.theme.StormBottom
import com.kampplus.hava.ui.theme.StormMid
import com.kampplus.hava.ui.theme.StormTop
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class SkyTone {
    PURPLE,
    RAIN,
    SNOW,
    STORM
}

fun weatherTone(weatherCode: Int): SkyTone = when {
    weatherCode in 95..99 -> SkyTone.STORM
    weatherCode in 71..77 || weatherCode in 85..86 -> SkyTone.SNOW
    weatherCode in 51..67 || weatherCode in 80..82 -> SkyTone.RAIN
    else -> SkyTone.PURPLE
}

@Composable
fun NightSkyBackground(
    tone: SkyTone = SkyTone.PURPLE,
    isDaytime: Boolean = false,
    weatherCode: Int? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val nightPalette = when (tone) {
        SkyTone.PURPLE -> Triple(NightBackground, NightMid, NightBottom)
        SkyTone.RAIN -> Triple(RainTop, RainMid, RainBottom)
        SkyTone.SNOW -> Triple(SnowTop, SnowMid, SnowBottom)
        SkyTone.STORM -> Triple(StormTop, StormMid, StormBottom)
    }
    val dayPalette = Triple(DaySkyTop, DaySkyMid, DaySkyBottom)
    val (top, middle, bottom) = if (isDaytime) dayPalette else nightPalette
    val animatedTop by animateColorAsState(top, tween(900), label = "skyTop")
    val animatedMiddle by animateColorAsState(middle, tween(900), label = "skyMiddle")
    val animatedBottom by animateColorAsState(bottom, tween(900), label = "skyBottom")
    val owner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var animationsEnabled by remember(context) { mutableStateOf(ValueAnimator.areAnimatorsEnabled()) }
    var isVisible by remember(owner) {
        mutableStateOf(owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
    }

    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, _ ->
            isVisible = owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            animationsEnabled = ValueAnimator.areAnimatorsEnabled()
        }
        val animationSettingObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                animationsEnabled = ValueAnimator.areAnimatorsEnabled()
            }
        }
        owner.lifecycle.addObserver(observer)
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            animationSettingObserver
        )
        onDispose {
            owner.lifecycle.removeObserver(observer)
            context.contentResolver.unregisterContentObserver(animationSettingObserver)
        }
    }

    Box(modifier = modifier) {
        if (isVisible && animationsEnabled) {
            AnimatedSkyCanvas(
                top = animatedTop,
                middle = animatedMiddle,
                bottom = animatedBottom,
                tone = tone,
                isDaytime = isDaytime,
                weatherCode = weatherCode,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Brush.verticalGradient(listOf(animatedTop, animatedMiddle, animatedBottom)))
                drawStars(STARS, STARS.map { 0.38f }, 0f, if (isDaytime) 0.10f else 1f)
            }
        }
        CompositionLocalProvider(LocalContentColor provides MainText) {
            content()
        }
    }
}

@Composable
private fun AnimatedSkyCanvas(
    top: Color,
    middle: Color,
    bottom: Color,
    tone: SkyTone,
    isDaytime: Boolean,
    weatherCode: Int?,
    modifier: Modifier
) {
    val transition = rememberInfiniteTransition(label = "nightSky")
    val stars = remember { createStars() }
    val alphas = stars.mapIndexed { index, star ->
        transition.animateFloat(
            initialValue = 0.18f,
            targetValue = 0.70f,
            animationSpec = infiniteRepeatable(
                animation = tween(star.durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "star$index"
        )
    }
    val shootingStar by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SHOOTING_STAR_PERIOD
                0f at 0
                0f at SHOOTING_STAR_PERIOD - SHOOTING_STAR_DURATION
                1f at SHOOTING_STAR_PERIOD
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shootingStar"
    )
    val rainProgress = if (tone == SkyTone.RAIN || tone == SkyTone.STORM) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(RAIN_CYCLE_MS, easing = LinearEasing)),
            label = "rainProgress"
        ).value
    } else {
        0f
    }
    val snowProgress = if (tone == SkyTone.SNOW) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(SNOW_CYCLE_MS, easing = LinearEasing)),
            label = "snowProgress"
        ).value
    } else {
        0f
    }
    val lightning = if (tone == SkyTone.STORM) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = LIGHTNING_CYCLE_MS
                    0f at 0
                    0f at 7_000
                    0.11f at 7_080
                    0.035f at 7_180
                    0f at 7_340
                    0f at LIGHTNING_CYCLE_MS
                }
            ),
            label = "lightning"
        ).value
    } else {
        0f
    }
    val cloudProgress = if (weatherCode in 1..3 || weatherCode == 45 || weatherCode == 48) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(CLOUD_CYCLE_MS, easing = LinearEasing)),
            label = "cloudProgress"
        ).value
    } else {
        0f
    }
    Canvas(modifier) {
        drawRect(Brush.verticalGradient(listOf(top, middle, bottom)))
        drawStars(stars, alphas.map { it.value }, shootingStar, if (isDaytime) 0.10f else 1f)
        if (weatherCode in 1..3 || weatherCode == 45 || weatherCode == 48) {
            drawClouds(cloudProgress)
            if (weatherCode == 45 || weatherCode == 48) {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.025f), Color.White.copy(alpha = 0.055f), Color.White.copy(alpha = 0.035f))
                    )
                )
            }
        }
        when (tone) {
            SkyTone.RAIN -> drawRain(rainProgress, RAIN_DROPS)
            SkyTone.SNOW -> drawSnow(snowProgress, SNOWFLAKES)
            SkyTone.STORM -> {
                drawRain(rainProgress, RAIN_DROPS)
                if (lightning > 0f) drawRect(Color.White.copy(alpha = lightning))
            }
            SkyTone.PURPLE -> Unit
        }
    }
}

private fun DrawScope.drawClouds(progress: Float) {
    CLOUDS.forEachIndexed { index, cloud ->
        val drift = ((progress + index * 0.38f).mod(1f) - 0.5f) * size.width * 0.16f
        val center = Offset(size.width * cloud.x + drift, size.height * cloud.y)
        val cloudSize = size.width * cloud.width
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFBFC8E8).copy(alpha = 0.11f), Color(0xFFBFC8E8).copy(alpha = 0f)),
                center = center,
                radius = cloudSize * 0.55f
            ),
            topLeft = Offset(center.x - cloudSize / 2f, center.y - cloudSize * 0.20f),
            size = Size(cloudSize, cloudSize * 0.40f)
        )
    }
}

private fun DrawScope.drawStars(stars: List<SkyStar>, alphas: List<Float>, shootingStar: Float, brightness: Float) {
    stars.forEachIndexed { index, star ->
        drawCircle(
            color = star.color.copy(alpha = alphas[index] * brightness),
            radius = star.radius * density,
            center = Offset(size.width * star.x, size.height * star.y)
        )
    }
    if (shootingStar > 0f) {
        val start = Offset(size.width * 0.82f, size.height * 0.18f)
        val end = Offset(size.width * (0.82f - shootingStar * 0.22f), size.height * (0.18f + shootingStar * 0.14f))
        drawLine(
            color = Color(0xFFFFE8A3).copy(alpha = (1f - shootingStar) * 0.85f * brightness),
            start = start,
            end = end,
            strokeWidth = 1.5f * density
        )
    }
}

private fun DrawScope.drawRain(progress: Float, drops: List<WeatherParticle>) {
    drops.forEach { drop ->
        val yProgress = (drop.phase + progress * drop.speed).mod(1f)
        val y = yProgress * size.height
        val x = (drop.x * size.width + y * RAIN_SLANT).mod(size.width)
        val length = drop.size * density
        drawLine(
            color = Color(0xFFCADBFF).copy(alpha = drop.alpha),
            start = Offset(x, y),
            end = Offset(x - length * RAIN_SLANT, y + length),
            strokeWidth = drop.stroke * density,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

private fun DrawScope.drawSnow(progress: Float, flakes: List<WeatherParticle>) {
    flakes.forEach { flake ->
        val yProgress = (flake.phase + progress * flake.speed).mod(1f)
        val y = yProgress * size.height
        val sway = sin((progress * 2.0 * PI + flake.phase * 2.0 * PI).toFloat()) *
            flake.sway * density
        val x = (flake.x * size.width + sway).mod(size.width)
        drawCircle(
            color = Color(0xFFE4EEFF).copy(alpha = flake.alpha),
            radius = flake.size * density,
            center = Offset(x, y)
        )
    }
}

private fun createStars(): List<SkyStar> {
    val random = Random(60425)
    return List(STAR_COUNT) {
        SkyStar(
            x = random.nextFloat(),
            y = random.nextFloat(),
            radius = 1f + random.nextFloat() * 1.5f,
            durationMillis = 4_000 + random.nextInt(4_001),
            color = if (it % 9 == 0) Color(0xFFFFD758) else Color(0xFFE1DDFB)
        )
    }
}

private data class SkyStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val durationMillis: Int,
    val color: Color
)

private data class WeatherParticle(
    val x: Float,
    val phase: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val stroke: Float = 0.8f,
    val sway: Float = 0f
)

private data class SkyCloud(val x: Float, val y: Float, val width: Float)

private fun createParticles(count: Int, seed: Int, snow: Boolean): List<WeatherParticle> {
    val random = Random(seed)
    return List(count) {
        WeatherParticle(
            x = random.nextFloat(),
            phase = random.nextFloat(),
            speed = if (snow) (1 + random.nextInt(2)).toFloat() else (1 + random.nextInt(3)).toFloat(),
            size = if (snow) 1.2f + random.nextFloat() * 2.0f else 8f + random.nextFloat() * 8f,
            alpha = if (snow) 0.22f + random.nextFloat() * 0.16f else 0.18f + random.nextFloat() * 0.09f,
            stroke = 0.6f + random.nextFloat() * 0.5f,
            sway = 6f + random.nextFloat() * 12f
        )
    }
}

private const val STAR_COUNT = 60
private const val SHOOTING_STAR_PERIOD = 25_000
private const val SHOOTING_STAR_DURATION = 800
private const val RAIN_CYCLE_MS = 2_800
private const val SNOW_CYCLE_MS = 12_000
private const val LIGHTNING_CYCLE_MS = 8_000
private const val CLOUD_CYCLE_MS = 90_000
private const val RAIN_SLANT = 0.08f
private val RAIN_DROPS = createParticles(70, 712, snow = false)
private val SNOWFLAKES = createParticles(50, 856, snow = true)
private val STARS = createStars()
private val CLOUDS = listOf(
    SkyCloud(0.16f, 0.24f, 0.46f),
    SkyCloud(0.72f, 0.43f, 0.40f),
    SkyCloud(0.36f, 0.72f, 0.34f)
)

@Preview(showBackground = true)
@Composable
private fun RainSkyPreview() {
    NightSkyBackground(tone = SkyTone.RAIN) {}
}

@Preview(showBackground = true)
@Composable
private fun SnowSkyPreview() {
    NightSkyBackground(tone = SkyTone.SNOW) {}
}

@Preview(showBackground = true)
@Composable
private fun StormSkyPreview() {
    NightSkyBackground(tone = SkyTone.STORM) {}
}

@Preview(showBackground = true)
@Composable
private fun DaySkyPreview() {
    NightSkyBackground(isDaytime = true) {}
}

@Preview(showBackground = true)
@Composable
private fun CloudySkyPreview() {
    NightSkyBackground(weatherCode = 3) {}
}

@Preview(showBackground = true)
@Composable
private fun FoggySkyPreview() {
    NightSkyBackground(weatherCode = 45) {}
}
