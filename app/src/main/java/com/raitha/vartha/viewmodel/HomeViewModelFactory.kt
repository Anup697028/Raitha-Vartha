package com.raitha.vartha.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raitha.vartha.data.local.AppDatabase
import com.raitha.vartha.data.local.PreferenceManager
import com.raitha.vartha.data.remote.WeatherService
import com.raitha.vartha.data.repository.CropRepository
import com.raitha.vartha.data.repository.FirebaseRepository
import com.raitha.vartha.data.repository.GeminiRepository
import com.raitha.vartha.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val weatherService = retrofit.create(WeatherService::class.java)
            val database = AppDatabase.getDatabase(context.applicationContext)
            val dao = database.raithaDao()
            val firebaseRepo = FirebaseRepository(dao)
            val cropRepo = CropRepository(dao)
            val geminiRepo = GeminiRepository(Constants.GEMINI_API_KEY)
            val prefManager = PreferenceManager(context.applicationContext)
            
            return HomeViewModel(firebaseRepo, cropRepo, dao, weatherService, geminiRepo, prefManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
