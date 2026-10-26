package com.kampplus.hava.ui.forecast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kampplus.hava.R
import com.kampplus.hava.model.CurrentWeather
import com.kampplus.hava.model.DailyForecast
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.model.HourlyForecast
import com.kampplus.hava.model.MoonPhaseInfo
import com.kampplus.hava.model.WeatherCondition
import com.kampplus.hava.model.moonPhase
import com.kampplus.hava.model.nightSkyGuide
import com.kampplus.hava.model.suggest
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.ErrorView
import com.kampplus.hava.ui.component.GlassCard
import com.kampplus.hava.ui.component.MoonOrb
import com.kampplus.hava.ui.component.NightSkyBackground
import com.kampplus.hava.ui.component.SkyTone
import com.kampplus.hava.ui.component.SunOrb
import com.kampplus.hava.ui.component.weatherTone
import com.kampplus.hava.ui.theme.AccentYellow
import com.kampplus.hava.ui.theme.GlassBorder
import com.kampplus.hava.ui.theme.HavaTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun ForecastRoute(onBack: () -> Unit, modifier: Modifier = Modifier, viewModel: ForecastViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForecastScreen(
        cityName = viewModel.cityName,
        subtitle = viewModel.subtitle,
        latitude = viewModel.latitude,
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

private enum class ForecastTab {
    TODAY,
    WEEK,
    DETAILS,
    SKY
}

@Composable
fun ForecastScreen(
    cityName: String,
    subtitle: String,
    latitude: Double,
    uiState: LoadState<Forecast>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(ForecastTab.TODAY) }
    val currentWeather = (uiState as? LoadState.Success)?.data?.current
    val isDaytime = currentWeather?.observedAt?.hour?.let { it in 6 until 18 } ?: false
    val tone = currentWeather?.weatherCode?.let(::weatherTone) ?: SkyTone.PURPLE

    NightSkyBackground(
        tone = tone,
        isDaytime = isDaytime,
        weatherCode = currentWeather?.weatherCode,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ForecastHeader(cityName = cityName, onBack = onBack)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState) {
                    LoadState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentYellow)
                    }
                    LoadState.Empty -> ErrorView(
                        message = stringResource(R.string.forecast_empty),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)
                    )
                    is LoadState.Error -> ErrorView(
                        message = uiState.message,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                        onRetry = onRetry
                    )
                    is LoadState.Success -> AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            fadeIn(tween(280)) togetherWith fadeOut(tween(220))
                        },
                        label = "forecastTab"
                    ) { tab ->
                        when (tab) {
                            ForecastTab.TODAY -> TodayContent(uiState.data)
                            ForecastTab.WEEK -> WeekContent(uiState.data)
                            ForecastTab.DETAILS -> DetailsContent(uiState.data)
                            ForecastTab.SKY -> SkyContent(uiState.data, latitude)
                        }
                    }
                }
            }
            ForecastBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    }
}

@Composable
private fun ForecastHeader(cityName: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = cityName.ifEmpty { stringResource(R.string.detail_title) },
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )
    }
}

@Composable
private fun TodayContent(forecast: Forecast) {
    val current = forecast.current
    val phase = remember(current.observedAt) { moonPhase(current.observedAt.toLocalDate()) }
    val condition = WeatherCondition.fromWeatherCode(current.weatherCode)
    val today = forecast.daily.firstOrNull()
    var targetTemperature by remember(current) { mutableIntStateOf(0) }
    val animatedTemperature by animateIntAsState(
        targetValue = targetTemperature,
        animationSpec = tween(900),
        label = "heroTemperature"
    )
    LaunchedEffect(current) { targetTemperature = current.temperatureC.roundToInt() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StaggeredEntry(index = 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                if (current.observedAt.hour in 6 until 18) {
                    SunOrb(orbSize = 104.dp)
                } else {
                    MoonOrb(phase = phase.phase.toFloat(), orbSize = 104.dp)
                }
                Text(
                    text = stringResource(R.string.temperature_degrees, animatedTemperature),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 94.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = condition.emoji, fontSize = 22.sp)
                    Text(text = condition.label, style = MaterialTheme.typography.titleMedium)
                }
                today?.let {
                    Text(
                        text = stringResource(
                            R.string.high_low_summary,
                            degrees(it.maxTemperatureC),
                            degrees(it.minTemperatureC)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MoonPhaseChip(phase)
            }
        }
        StaggeredEntry(index = 1) {
            ClothingTipCard(current)
        }
        StaggeredEntry(index = 2) {
            HourlyForecastCard(current, forecast.hourly)
        }
    }
}

@Composable
private fun ClothingTipCard(current: CurrentWeather) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🧥", fontSize = 24.sp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.clothing_title), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = suggest(current.temperatureC, current.weatherCode, current.windSpeedKmh),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HourlyForecastCard(current: CurrentWeather, hours: List<HourlyForecast>) {
    val currentHour = current.observedAt.withMinute(0).withSecond(0).withNano(0)
    val upcoming = hours
        .filter { !it.time.isBefore(currentHour) }
        .take(HOURLY_COUNT)
    if (upcoming.isEmpty()) return

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.hourly_label),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(upcoming, key = { _, hour -> hour.time.toString() }) { index, hour ->
                    val selected = index == 0
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White.copy(alpha = if (selected) 0.12f else 0.055f))
                            .then(if (selected) Modifier.border(1.dp, AccentYellow, RoundedCornerShape(28.dp)) else Modifier)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (selected) stringResource(R.string.now) else hour.time.format(HOUR_FORMAT),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) AccentYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            WeatherCondition.fromWeatherCode(hour.weatherCode).emoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(degrees(hour.temperatureC), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekContent(forecast: Forecast) {
    val current = forecast.current
    val minimum = forecast.daily.minOfOrNull { it.minTemperatureC } ?: current.temperatureC
    val maximum = forecast.daily.maxOfOrNull { it.maxTemperatureC } ?: current.temperatureC
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        forecast.daily.firstOrNull()?.let { today ->
            StaggeredEntry(index = 0) {
                Text(
                    text = stringResource(
                        R.string.forecast_today_summary,
                        degrees(current.temperatureC),
                        WeatherCondition.fromWeatherCode(current.weatherCode).label,
                        degrees(today.maxTemperatureC),
                        degrees(today.minTemperatureC)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        StaggeredEntry(index = 1) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    forecast.daily.take(7).forEachIndexed { index, day ->
                        StaggeredEntry(index = index) {
                            DailyForecastRow(
                                day = day,
                                isToday = index == 0,
                                minimum = minimum,
                                maximum = maximum
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyForecastRow(day: DailyForecast, isToday: Boolean, minimum: Double, maximum: Double) {
    val label = if (isToday) {
        stringResource(R.string.today)
    } else {
        day.date.format(DAY_FORMAT)
            .replaceFirstChar { it.titlecase(Locale.forLanguageTag("tr")) }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                color = if (isToday) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.85f),
            style = MaterialTheme.typography.labelLarge,
            color = if (isToday) AccentYellow else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = WeatherCondition.fromWeatherCode(day.weatherCode).emoji,
            modifier = Modifier.weight(0.55f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Text(
            text = degrees(day.minTemperatureC),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TemperatureRangeBar(
            low = day.minTemperatureC,
            high = day.maxTemperatureC,
            minimum = minimum,
            maximum = maximum,
            modifier = Modifier.weight(1.8f)
        )
        Text(
            text = degrees(day.maxTemperatureC),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TemperatureRangeBar(low: Double, high: Double, minimum: Double, maximum: Double, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(10.dp)) {
        val barHeight = size.height * 0.55f
        val top = (size.height - barHeight) / 2f
        val range = (maximum - minimum).coerceAtLeast(1.0)
        val start = ((low - minimum) / range).toFloat().coerceIn(0f, 1f)
        val end = ((high - minimum) / range).toFloat().coerceIn(start, 1f)
        drawRoundRect(
            color = Color(0xFF160F36),
            topLeft = Offset(0f, top),
            size = Size(size.width, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2f)
        )
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(Color(0xFF8052FF), Color(0xFFE1C7FF), AccentYellow)),
            topLeft = Offset(size.width * start, top),
            size = Size((size.width * (end - start)).coerceAtLeast(barHeight), barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2f)
        )
    }
}

@Composable
private fun DetailsContent(forecast: Forecast) {
    val current = forecast.current
    val today = forecast.daily.firstOrNull()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailMetricCard(
                icon = "💧",
                title = stringResource(R.string.detail_humidity),
                value = current.humidityPercent?.let { "%$it" } ?: "—",
                modifier = Modifier.weight(1f)
            )
            DetailMetricCard(
                icon = "➤",
                title = stringResource(R.string.detail_wind),
                value = current.windSpeedKmh?.let { "${it.roundToInt()} km/sa" } ?: "—",
                suffix = current.windDirectionDegrees?.let(::windDirectionLabel) ?: "—",
                windDirection = current.windDirectionDegrees,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailMetricCard(
                icon = "🌧",
                title = stringResource(R.string.detail_precipitation),
                value = current.precipitationMm?.let { "%.1f mm".format(Locale.forLanguageTag("tr"), it) } ?: "—",
                modifier = Modifier.weight(1f)
            )
            DetailMetricCard(
                icon = "🌡",
                title = stringResource(R.string.detail_feels_like),
                value = current.apparentTemperatureC?.let(::degrees) ?: "—",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailMetricCard(
                icon = "☀",
                title = stringResource(R.string.detail_uv),
                value = current.uvIndex?.let { "%.1f".format(Locale.forLanguageTag("tr"), it) } ?: "—",
                modifier = Modifier.weight(1f)
            )
            DetailMetricCard(
                icon = "◉",
                title = stringResource(R.string.detail_pressure),
                value = current.surfacePressureHpa?.let { "${it.roundToInt()} hPa" } ?: "—",
                modifier = Modifier.weight(1f)
            )
        }
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(
                        R.string.sunrise_sunset,
                        today?.sunrise?.format(HOUR_FORMAT) ?: "—",
                        today?.sunset?.format(HOUR_FORMAT) ?: "—"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DetailMetricCard(
    icon: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    windDirection: Int? = null
) {
    GlassCard(
        modifier = modifier.height(148.dp),
        color = Color.White.copy(alpha = 0.055f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = icon,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    color = AccentYellow,
                    fontSize = 19.sp
                )
                if (suffix != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WindCompass(windDirection)
                        Text(
                            text = suffix,
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(value, style = MaterialTheme.typography.headlineSmall)
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WindCompass(directionDegrees: Int?) {
    Canvas(Modifier.size(24.dp)) {
        drawCircle(
            color = AccentYellow.copy(alpha = 0.8f),
            radius = size.minDimension * 0.43f,
            style = Stroke(width = 1.4.dp.toPx())
        )
        if (directionDegrees != null) {
            rotate(directionDegrees.toFloat()) {
                val tip = Offset(center.x, size.height * 0.19f)
                val tail = Offset(center.x, size.height * 0.76f)
                drawLine(
                    color = AccentYellow,
                    start = tail,
                    end = tip,
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = AccentYellow,
                    start = tip,
                    end = Offset(tip.x - size.width * 0.12f, tip.y + size.height * 0.2f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = AccentYellow,
                    start = tip,
                    end = Offset(tip.x + size.width * 0.12f, tip.y + size.height * 0.2f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private fun windDirectionLabel(degrees: Int): String {
    val directions = listOf("K", "KD", "D", "GD", "G", "GB", "B", "KB")
    return directions[((degrees + 22) / 45).mod(8)]
}

@Composable
private fun SkyContent(forecast: Forecast, latitude: Double) {
    val current = forecast.current
    val phase = remember(current.observedAt) { moonPhase(current.observedAt.toLocalDate()) }
    val skyGuide = remember(current.observedAt.monthValue, latitude) {
        nightSkyGuide(current.observedAt.monthValue, latitude)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StaggeredEntry(index = 0) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MoonOrb(phase = phase.phase.toFloat(), orbSize = 144.dp)
                MoonPhaseChip(phase)
                Text(
                    text = stringResource(R.string.moon_age, phase.ageDays),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        StaggeredEntry(index = 1) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("✦", color = AccentYellow, fontSize = 26.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.sky_tonight_title), style = MaterialTheme.typography.titleMedium)
                        Text(skyGuide.constellation, style = MaterialTheme.typography.titleMedium, color = AccentYellow)
                        Text(
                            text = stringResource(R.string.sky_stars_label, skyGuide.stars.joinToString(", ")),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            skyGuide.note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            stringResource(R.string.sky_observation_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoonPhaseChip(phase: MoonPhaseInfo) {
    Row(
        modifier = Modifier
            .background(Color(0xFF261B4B).copy(alpha = 0.82f), CircleShape)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = AccentYellow) {}
        Text(phase.name, style = MaterialTheme.typography.labelLarge)
        Text(
            stringResource(R.string.moon_illumination, phase.illuminationPercent),
            style = MaterialTheme.typography.labelLarge,
            color = AccentYellow
        )
    }
}

@Composable
private fun StaggeredEntry(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(index) {
        delay(index * CONTENT_STAGGER_MS)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 12 },
        exit = fadeOut(tween(160))
    ) {
        content()
    }
}

@Composable
private fun ForecastBottomBar(selectedTab: ForecastTab, onTabSelected: (ForecastTab) -> Unit) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        containerColor = Color(0xE61F1945),
        contentColor = MaterialTheme.colorScheme.onSurface,
        windowInsets = WindowInsets(0.dp)
    ) {
        ForecastTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(width = 54.dp, height = 32.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) Color(0x40FFC900) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        TabOutlineIcon(tab, selected)
                    }
                },
                label = {
                    Text(
                        text = stringResource(tab.labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentYellow,
                    selectedTextColor = AccentYellow,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private val ForecastTab.labelRes: Int
    get() = when (this) {
        ForecastTab.TODAY -> R.string.tab_today
        ForecastTab.WEEK -> R.string.tab_week
        ForecastTab.DETAILS -> R.string.tab_details
        ForecastTab.SKY -> R.string.tab_sky
    }

@Composable
private fun TabOutlineIcon(tab: ForecastTab, selected: Boolean) {
    val tint = if (selected) AccentYellow else MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(Modifier.size(22.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        when (tab) {
            ForecastTab.TODAY -> {
                drawCircle(tint, radius = size.minDimension * 0.22f, style = stroke)
                for (index in 0 until 8) {
                    val angle = index * (Math.PI / 4.0)
                    val inner = size.minDimension * 0.34f
                    val outer = size.minDimension * 0.46f
                    drawLine(
                        tint,
                        Offset(center.x + kotlin.math.cos(angle).toFloat() * inner, center.y + kotlin.math.sin(angle).toFloat() * inner),
                        Offset(center.x + kotlin.math.cos(angle).toFloat() * outer, center.y + kotlin.math.sin(angle).toFloat() * outer),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round
                    )
                }
            }
            ForecastTab.WEEK -> {
                val left = size.width * 0.16f
                val top = size.height * 0.18f
                val width = size.width * 0.68f
                val height = size.height * 0.68f
                drawRoundRect(
                    tint,
                    Offset(left, top),
                    Size(width, height),
                    androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                    style = stroke
                )
                drawLine(tint, Offset(left, top + height * 0.28f), Offset(left + width, top + height * 0.28f), strokeWidth = stroke.width)
                drawLine(
                    tint,
                    Offset(size.width * 0.34f, top),
                    Offset(size.width * 0.34f, top + height * 0.18f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
                drawLine(
                    tint,
                    Offset(size.width * 0.66f, top),
                    Offset(size.width * 0.66f, top + height * 0.18f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
            }
            ForecastTab.DETAILS -> {
                val cell = size.minDimension * 0.25f
                for (row in 0..1) {
                    for (column in 0..1) {
                        drawRoundRect(
                            tint,
                            Offset(size.width * 0.18f + column * cell * 1.45f, size.height * 0.15f + row * cell * 1.45f),
                            Size(cell, cell),
                            androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                            style = stroke
                        )
                    }
                }
                drawLine(
                    tint,
                    Offset(size.width * 0.5f, size.height * 0.5f),
                    Offset(size.width * 0.5f, size.height * 0.84f),
                    strokeWidth = stroke.width
                )
            }
            ForecastTab.SKY -> {
                val star = Path().apply {
                    moveTo(size.width * 0.50f, size.height * 0.08f)
                    lineTo(size.width * 0.60f, size.height * 0.39f)
                    lineTo(size.width * 0.92f, size.height * 0.50f)
                    lineTo(size.width * 0.60f, size.height * 0.61f)
                    lineTo(size.width * 0.50f, size.height * 0.92f)
                    lineTo(size.width * 0.40f, size.height * 0.61f)
                    lineTo(size.width * 0.08f, size.height * 0.50f)
                    lineTo(size.width * 0.40f, size.height * 0.39f)
                    close()
                }
                drawPath(star, tint, style = stroke)
            }
        }
    }
}

private fun degrees(value: Double): String = "%d°".format(Locale.forLanguageTag("tr"), value.roundToInt())

private const val HOURLY_COUNT = 24
private const val CONTENT_STAGGER_MS = 60L
private val HOUR_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("tr"))

@Preview(showBackground = true)
@Composable
private fun ForecastScreenPreview() {
    HavaTheme {
        ForecastScreen(
            cityName = "İstanbul",
            subtitle = "İstanbul, Türkiye",
            latitude = 41.0082,
            uiState = LoadState.Success(
                Forecast(
                    current = CurrentWeather(
                        temperatureC = 21.0,
                        weatherCode = 2,
                        observedAt = LocalDateTime.of(2026, 10, 25, 14, 0)
                    ),
                    hourly = List(8) {
                        HourlyForecast(
                            time = LocalDateTime.of(2026, 10, 25, 14, 0).plusHours(it.toLong()),
                            temperatureC = 18.0 + it,
                            weatherCode = 2
                        )
                    },
                    daily = List(7) {
                        DailyForecast(
                            date = LocalDate.of(2026, 10, 25).plusDays(it.toLong()),
                            minTemperatureC = 9.0 + it,
                            maxTemperatureC = 19.0 + it,
                            weatherCode = 61
                        )
                    }
                )
            ),
            onBack = {},
            onRetry = {}
        )
    }
}
