package com.raitha.vartha.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class PreferenceManager(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val DISTRICT_KEY = stringPreferencesKey("district")
        val THEME_KEY = stringPreferencesKey("theme")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "kn" }
    val districtFlow: Flow<String?> = context.dataStore.data.map { it[DISTRICT_KEY] }
    val themeFlow: Flow<String> = context.dataStore.data.map { it[THEME_KEY] ?: "system" }
    val onboardingFlow: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun saveDistrict(district: String) {
        context.dataStore.edit { it[DISTRICT_KEY] = district }
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }
}
