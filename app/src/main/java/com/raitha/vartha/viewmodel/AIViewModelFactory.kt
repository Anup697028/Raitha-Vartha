package com.raitha.vartha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raitha.vartha.data.repository.GeminiRepository

class AIViewModelFactory(private val apiKey: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIViewModel::class.java)) {
            val repo = GeminiRepository(apiKey)
            return AIViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
