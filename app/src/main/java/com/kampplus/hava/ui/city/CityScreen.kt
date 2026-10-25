package com.kampplus.hava.ui.city

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kampplus.hava.R
import com.kampplus.hava.model.City
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.EmptyView
import com.kampplus.hava.ui.component.ErrorView
import com.kampplus.hava.ui.component.GlassCard
import com.kampplus.hava.ui.component.NightSkyBackground
import com.kampplus.hava.ui.component.ShimmerList
import com.kampplus.hava.ui.theme.AccentYellow
import com.kampplus.hava.ui.theme.GlassBorder
import com.kampplus.hava.ui.theme.GlassFill
import com.kampplus.hava.ui.theme.HavaTheme
import com.kampplus.hava.ui.theme.MainText
import kotlinx.coroutines.delay

private const val CITY_ITEM_STAGGER_MS = 60L
private const val MIN_SEARCH_LENGTH = 3

@Composable
fun CityRoute(onCityClick: (City) -> Unit, modifier: Modifier = Modifier, viewModel: CityViewModel = viewModel(), onBack: () -> Unit = {}) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var previousQuery by remember { mutableStateOf(query) }
    LaunchedEffect(query) {
        if (query == previousQuery) return@LaunchedEffect
        delay(300)
        if (query.length >= MIN_SEARCH_LENGTH) viewModel.search()
        previousQuery = query
    }

    CityScreen(
        query = query,
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::search,
        onCityClick = onCityClick,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun CityScreen(
    query: String,
    uiState: LoadState<List<City>>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    NightSkyBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = stringResource(R.string.city_search_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                if (query.length < MIN_SEARCH_LENGTH) {
                    SearchPrompt()
                } else {
                    when (uiState) {
                        LoadState.Loading -> LoadingSearch()
                        is LoadState.Success -> if (query.length >= MIN_SEARCH_LENGTH) {
                            CityList(cities = uiState.data, onCityClick = onCityClick)
                        } else {
                            SearchPrompt()
                        }
                        LoadState.Empty -> if (query.length < MIN_SEARCH_LENGTH && query.isNotEmpty()) {
                            SearchPrompt()
                        } else {
                            EmptyView(
                                title = stringResource(R.string.city_not_found),
                                message = stringResource(R.string.city_not_found_message),
                                suggestions = listOf(
                                    stringResource(R.string.city_suggestion_istanbul),
                                    stringResource(R.string.city_suggestion_ankara),
                                    stringResource(R.string.city_suggestion_izmir)
                                ),
                                onSuggestionClick = { city ->
                                    onQueryChange(city)
                                    if (city.length >= MIN_SEARCH_LENGTH) onSearch()
                                }
                            )
                        }
                        is LoadState.Error -> ErrorView(
                            message = uiState.message,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                            onRetry = onSearch
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_hint), color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = AccentYellow)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.action_clear),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MainText,
            unfocusedTextColor = MainText,
            disabledTextColor = MainText.copy(alpha = 0.6f),
            errorTextColor = MainText,
            focusedContainerColor = GlassFill,
            unfocusedContainerColor = GlassFill,
            disabledContainerColor = GlassFill,
            errorContainerColor = GlassFill,
            focusedBorderColor = GlassBorder,
            unfocusedBorderColor = GlassBorder,
            disabledBorderColor = GlassBorder,
            errorBorderColor = GlassBorder,
            focusedPlaceholderColor = MainText.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = MainText.copy(alpha = 0.6f),
            disabledPlaceholderColor = MainText.copy(alpha = 0.4f),
            errorPlaceholderColor = MainText.copy(alpha = 0.6f),
            focusedLeadingIconColor = AccentYellow,
            unfocusedLeadingIconColor = AccentYellow,
            disabledLeadingIconColor = MainText.copy(alpha = 0.4f),
            errorLeadingIconColor = AccentYellow,
            focusedTrailingIconColor = MainText.copy(alpha = 0.7f),
            unfocusedTrailingIconColor = MainText.copy(alpha = 0.7f),
            disabledTrailingIconColor = MainText.copy(alpha = 0.4f),
            errorTrailingIconColor = MainText.copy(alpha = 0.7f),
            cursorColor = AccentYellow,
            errorCursorColor = AccentYellow
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                if (query.length >= MIN_SEARCH_LENGTH) onSearch()
            }
        )
    )
}

@Composable
private fun SearchPrompt() {
    Text(
        text = stringResource(R.string.city_search_minimum),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun LoadingSearch() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = AccentYellow,
                strokeWidth = 2.dp
            )
            Text(
                text = stringResource(R.string.city_searching),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ShimmerList(modifier = Modifier.fillMaxWidth())
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = AccentYellow,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(stringResource(R.string.location_hint_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.location_hint_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CityList(cities: List<City>, onCityClick: (City) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items = cities, key = { _, city -> city.id }) { index, city ->
            var visible by remember(city.id) { mutableStateOf(false) }
            LaunchedEffect(city.id) {
                delay(index * CITY_ITEM_STAGGER_MS)
                visible = true
            }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(260)) + slideInVertically(tween(260)) { it / 8 }
            ) {
                CityCard(city = city, onClick = { onCityClick(city) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityScreenPreview() {
    HavaTheme {
        CityScreen(
            query = "Ankara",
            uiState = LoadState.Success(listOf(City(323786, "Ankara", 39.91987, 32.85427, "TR", "Türkiye", "Ankara"))),
            onQueryChange = {},
            onSearch = {},
            onCityClick = {}
        )
    }
}
