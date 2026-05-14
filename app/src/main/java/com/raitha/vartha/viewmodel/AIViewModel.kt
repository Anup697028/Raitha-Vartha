package com.raitha.vartha.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.vartha.data.repository.GeminiRepository
import com.raitha.vartha.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AIViewModel(private val geminiRepository: GeminiRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Namaste! I am your AI Agriculture Expert. How can I help you today?", false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        val userMsg = ChatMessage(text, true)
        _messages.value = _messages.value + userMsg
        
        viewModelScope.launch {
            _isTyping.value = true
            try {
                val response = geminiRepository.askExpert(text)
                val aiMsg = ChatMessage(response ?: "I'm sorry, I couldn't process that. Please try again or check your internet connection.", false)
                _messages.value = _messages.value + aiMsg
            } catch (e: Exception) {
                Log.e("AIViewModel", "Error in sendMessage", e)
                _messages.value = _messages.value + ChatMessage("Connection Error: ${e.localizedMessage}. Please try again later.", false)
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun analyzeCropImage(bitmap: Bitmap) {
        val userMsg = ChatMessage("Analyzing image for diseases...", true)
        _messages.value = _messages.value + userMsg
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = geminiRepository.analyzeDisease(bitmap)
                val aiMsg = ChatMessage(result ?: "Unable to analyze the image.", false)
                _messages.value = _messages.value + aiMsg
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Analysis Error: ${e.localizedMessage}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
