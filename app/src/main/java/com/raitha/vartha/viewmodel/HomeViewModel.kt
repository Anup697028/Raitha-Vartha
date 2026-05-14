package com.raitha.vartha.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.data.local.RaithaDao
import com.raitha.vartha.data.remote.WeatherResponse
import com.raitha.vartha.data.remote.WeatherService
import com.raitha.vartha.data.repository.CropRepository
import com.raitha.vartha.data.repository.FirebaseRepository
import com.raitha.vartha.data.repository.GeminiRepository
import com.raitha.vartha.models.Tip
import com.raitha.vartha.models.SuccessStory
import com.raitha.vartha.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val cropRepository: CropRepository,
    private val raithaDao: RaithaDao,
    private val weatherService: WeatherService,
    private val geminiRepository: GeminiRepository,
    private val prefManager: PreferenceManager
) : ViewModel() {

    private val _rawTips = MutableStateFlow<List<Tip>>(emptyList())
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    val tips: StateFlow<List<Tip>> = combine(_rawTips, prefManager.languageFlow) { tips, language ->
        tips.map { tip ->
            if (language == "kn") {
                tip.copy(
                    cropName = tip.cropNameKn,
                    tipTitle = tip.tipTitleKn,
                    tipDescription = tip.tipDescriptionKn,
                    weatherAlert = tip.weatherAlertKn ?: tip.weatherAlert,
                    aiRecommendation = tip.aiRecommendationKn ?: tip.aiRecommendation
                )
            } else {
                tip
            }
        }
    }.flowOn(Dispatchers.Default)
     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    val currentDistrict: Flow<String?> = prefManager.districtFlow

    val savedTips: StateFlow<List<Tip>> = raithaDao.getSavedTips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStories: StateFlow<List<SuccessStory>> = combine(
        raithaDao.getAllSuccessStories(),
        prefManager.languageFlow
    ) { stories, language ->
        stories.map { story ->
            if (language == "kn") {
                story.copy(
                    district = story.districtKn,
                    cropType = story.cropTypeKn,
                    story = story.storyKn,
                    // We keep the detailed fields as they are for now or could add bilingual support if available
                    // For now, these stories are manually localized in our seed data
                )
            } else {
                story
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeDistrict()
        seedData()
        observeLocalTips()
    }

    private fun seedData() {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("HomeViewModel", "Starting data seeding...")
            // Centralized seeding of 30 crops and 30 matching tips
            cropRepository.seedCropsIfEmpty()
            // Seed success stories via firebase repository
            firebaseRepository.seedTipsIfEmpty()
            android.util.Log.d("HomeViewModel", "Data seeding completed.")

            // Optional: Still sync with Firebase for dynamic updates
            firebaseRepository.getDailyTips().collect {
                _isLoading.value = false
            }
        }
    }

    private fun observeDistrict() {
        viewModelScope.launch {
            prefManager.districtFlow.collectLatest { district ->
                district?.let {
                    fetchWeather(it, Constants.WEATHER_API_KEY)
                }
            }
        }
    }

    private fun observeLocalTips() {
        viewModelScope.launch {
            raithaDao.getAllTips().collect { localTips ->
                _rawTips.value = localTips
            }
        }
    }

    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = weatherService.getCurrentWeather(city, apiKey)
                _weather.value = response
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching weather", e)
            }
        }
    }

    fun toggleSaveTip(tip: Tip) {
        viewModelScope.launch {
            raithaDao.updateTip(tip.copy(isSaved = !tip.isSaved))
        }
    }
}
