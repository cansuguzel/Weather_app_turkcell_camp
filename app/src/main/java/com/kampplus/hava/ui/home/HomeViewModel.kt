package com.kampplus.hava.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.kampplus.hava.data.OpenMeteoWeatherRepository
import com.kampplus.hava.data.WeatherRepository
import com.kampplus.hava.model.City
import com.kampplus.hava.model.Forecast
import com.kampplus.hava.ui.LoadState
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.HttpException

data class HomeCityWeather(
    val city: City,
    val state: LoadState<Forecast> = LoadState.Loading
)

class HomeViewModel(
    private val repository: WeatherRepository = OpenMeteoWeatherRepository()
) : ViewModel() {
    private val _cities = kotlinx.coroutines.flow.MutableStateFlow(HOME_CITIES.map(::HomeCityWeather))
    val cities: kotlinx.coroutines.flow.StateFlow<List<HomeCityWeather>> = _cities

    init {
        refreshAll()
    }

    fun retry(city: City) {
        viewModelScope.launch { loadCity(city) }
    }

    private fun refreshAll() {
        viewModelScope.launch {
            supervisorScope {
                HOME_CITIES.map { city ->
                    async { loadCity(city) }
                }.awaitAll()
            }
        }
    }

    private suspend fun loadCity(city: City) {
        updateState(city, LoadState.Loading)
        val state = try {
            LoadState.Success(repository.getForecast(city.latitude, city.longitude))
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            Log.e(TAG, "Ana sayfa tahmini alınamadı: bağlantı hatası", exception)
            LoadState.Error("Hava durumu verilerine şu an ulaşılamıyor.")
        } catch (exception: HttpException) {
            Log.e(TAG, "Ana sayfa tahmini alınamadı: HTTP ${exception.code()}", exception)
            LoadState.Error("Hava durumu verilerine şu an ulaşılamıyor.")
        } catch (exception: JsonSyntaxException) {
            Log.e(TAG, "Ana sayfa tahmini çözümlenemedi", exception)
            LoadState.Error("Hava durumu verilerine şu an ulaşılamıyor.")
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "Ana sayfa tahmini eksik veri içeriyor", exception)
            LoadState.Error("Hava durumu verilerine şu an ulaşılamıyor.")
        }
        updateState(city, state)
    }

    private fun updateState(city: City, state: LoadState<Forecast>) {
        _cities.value = _cities.value.map { item ->
            if (item.city.id == city.id) item.copy(state = state) else item
        }
    }

    private companion object {
        const val TAG = "HomeViewModel"
        val HOME_CITIES = listOf(
            City(1, "İstanbul", 41.0082, 28.9784, "TR", "Türkiye", "Marmara"),
            City(2, "Ankara", 39.9334, 32.8597, "TR", "Türkiye", "İç Anadolu"),
            City(3, "Tokyo", 35.6762, 139.6503, "JP", "Japonya", "Tokyo"),
            City(4, "Londra", 51.5072, -0.1276, "GB", "Birleşik Krallık", "İngiltere"),
            City(5, "New York", 40.7128, -74.0060, "US", "ABD", "New York"),
            City(6, "Sidney", -33.8688, 151.2093, "AU", "Avustralya", "Yeni Güney Galler")
        )
    }
}
