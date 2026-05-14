package com.raitha.vartha.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(private val apiKey: String) {

    init {
        Log.d("GeminiRepository", "Initializing with Firebase AI")
    }

    private val textModel = Firebase.ai.generativeModel(
        modelName = "gemini-2.5-flash-lite"
    )

    suspend fun summarizeAdvisory(advisory: String): String? = withContext(Dispatchers.IO) {
        try {
            val prompt = "Summarize this agricultural advisory into 2 short, actionable points for a farmer in simple Kannada:\n$advisory"
            val response = textModel.generateContent(prompt)
            val result = response.text
            result
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error summarizing advisory: ${e.message}", e)
            "Unable to summarize. ${e.localizedMessage}"
        }
    }

    suspend fun analyzeDisease(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val inputContent = content {
                image(bitmap)
                text("Identify the crop disease in this image. Provide the disease name, confidence, cause, treatment, and prevention tips in a structured farmer-friendly format in Kannada and English.")
            }
            val response = textModel.generateContent(inputContent)
            val result = response.text
            result
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error analyzing disease: ${e.message}", e)
            "Error: ${e.localizedMessage ?: "Unable to analyze image"}. Please ensure AI services are enabled in your Firebase console."
        }
    }

    suspend fun askExpert(query: String): String? = withContext(Dispatchers.IO) {
        try {
            val prompt = "You are an expert agricultural AI assistant named Raitha-Vartha AI. Answer the following query in a helpful, friendly way for a farmer. Provide the answer in both Kannada and English if applicable:\n$query"
            val response = textModel.generateContent(prompt)
            val result = response.text
            result
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error asking expert: ${e.message}", e)
            "Error: ${e.localizedMessage ?: "Unable to reach AI service"}. Please check your Firebase project configuration."
        }
    }
}
