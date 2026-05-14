package com.raitha.vartha.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raitha.vartha.data.local.AppDatabase
import com.raitha.vartha.data.repository.CropRepository

class CropsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropsViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context.applicationContext)
            val repository = CropRepository(database.raithaDao())
            val prefManager = com.raitha.vartha.data.local.PreferenceManager(context.applicationContext)
            return CropsViewModel(repository, prefManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
