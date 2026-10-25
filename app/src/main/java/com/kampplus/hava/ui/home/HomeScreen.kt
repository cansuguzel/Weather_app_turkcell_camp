package com.kampplus.hava.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kampplus.hava.R
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.model.WeatherCondition
import com.kampplus.hava.model.moonPhase
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.GlassCard
import com.kampplus.hava.ui.component.MoonOrb
import com.kampplus.hava.ui.component.NightSkyBackground
import com.kampplus.hava.ui.theme.AccentYellow
import com.kampplus.hava.ui.theme.HavaTheme
import java.time.LocalDate
import kotlinx.coroutines.delay

private const val HOME_STAGGER_MS = 60L

@Composable
fun HomeRoute(
    onCitySelected: (HomeCityWeather) -> Unit,
    onChooseCity: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val cities by viewModel.cities.collectAsStateWithLifecycle()
    HomeScreen(
        cities = cities,
        onCitySelected = { item ->
            if (item.state is LoadState.Error) viewModel.retry(item.city) else onCitySelected(item)
        },
        onChooseCity = onChooseCity,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    cities: List<HomeCityWeather>,
    onCitySelected: (HomeCityWeather) -> Unit,
    onChooseCity: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phase = remember { moonPhase(LocalDate.now()) }
    NightSkyBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MoonOrb(phase = phase.phase.toFloat(), orbSize = 52.dp)
            }
            Row(
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 24.dp)
                    .clip(CircleShape)
                    .background(ColorResource.moonChip)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("☾", color = AccentYellow, fontSize = 20.sp)
                Text(
                    text = "${phase.name} · ${phase.illuminationPercent}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_world_summary),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.home_live),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentYellow
                )
            }
            if (cities.all { it.state is LoadState.Loading }) {
                HomeCityShimmer()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    cities.chunked(2).forEachIndexed { rowIndex, row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEachIndexed { columnIndex, item ->
                                val index = rowIndex * 2 + columnIndex
                                AnimatedHomeCityCard(
                                    item = item,
                                    index = index,
                                    onClick = { onCitySelected(item) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            Button(
                onClick = onChooseCity,
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 24.dp).height(56.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentYellow,
                    contentColor = androidx.compose.ui.graphics.Color(0xFF392800)
                )
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
                Text(
                    text = stringResource(R.string.home_choose_city),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun AnimatedHomeCityCard(item: HomeCityWeather, index: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var visible by remember(item.city.id) { mutableStateOf(false) }
    LaunchedEffect(item.city.id) {
        delay(index * HOME_STAGGER_MS)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 10 }
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "homeCardScale")
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.12f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
            color = ColorResource.card
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val forecast = (item.state as? LoadState.Success<Forecast>)?.data
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.city.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = forecast?.let { WeatherCondition.fromWeatherCode(it.current.weatherCode).label }
                                ?: stringResource(R.string.home_weather_unavailable),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = forecast?.let { WeatherCondition.fromWeatherCode(it.current.weatherCode).emoji } ?: "☁",
                        fontSize = 23.sp
                    )
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                    when (item.state) {
                        LoadState.Loading -> CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = AccentYellow,
                            strokeWidth = 2.dp
                        )
                        is LoadState.Error -> Text(
                            text = "—",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        is LoadState.Success -> Text(
                            text = "${item.state.data.current.temperatureC.toInt()}°",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        LoadState.Empty -> Text("—", style = MaterialTheme.typography.headlineMedium)
                    }
                    if (forecast != null) {
                        Text(
                            text = stringResource(
                                R.string.home_high_low,
                                forecast.daily.firstOrNull()?.maxTemperatureC?.let { "${it.toInt()}°" } ?: "—",
                                forecast.daily.firstOrNull()?.minTemperatureC?.let { "${it.toInt()}°" } ?: "—"
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (item.state is LoadState.Error) {
                        Text(
                            text = stringResource(R.string.action_retry),
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentYellow
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCityShimmer() {
    val shimmerOffset by rememberInfiniteTransition(label = "homeShimmer").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "homeShimmerOffset"
    )
    val brush = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0.04f), Color.White.copy(alpha = 0.13f), Color.White.copy(alpha = 0.04f)),
        start = Offset(shimmerOffset - 120f, 0f),
        end = Offset(shimmerOffset, 180f)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    GlassCard(
                        modifier = Modifier.weight(1f).aspectRatio(1.12f),
                        color = ColorResource.card
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                                Box(Modifier.fillMaxWidth(0.62f).height(18.dp).clip(RoundedCornerShape(10.dp)).background(brush))
                                Box(Modifier.fillMaxWidth(0.78f).height(14.dp).clip(RoundedCornerShape(10.dp)).background(brush))
                            }
                            Box(Modifier.fillMaxWidth(0.45f).height(30.dp).clip(RoundedCornerShape(12.dp)).background(brush))
                        }
                    }
                }
            }
        }
    }
}

private object ColorResource {
    val moonChip = androidx.compose.ui.graphics.Color(0x66261B4B)
    val card = androidx.compose.ui.graphics.Color(0x66201A48)
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HavaTheme {
        HomeScreen(emptyList(), onCitySelected = {}, onChooseCity = {})
    }
}
