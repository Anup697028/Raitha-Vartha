package com.raitha.vartha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.vartha.data.repository.CropRepository
import com.raitha.vartha.models.Crop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CropDetailViewModel(
    private val cropRepository: CropRepository,
    private val cropId: String
) : ViewModel() {

    private val _crop = MutableStateFlow<Crop?>(null)
    val crop: StateFlow<Crop?> = _crop.asStateFlow()

    init {
        fetchCropDetails()
    }

    private fun fetchCropDetails() {
        viewModelScope.launch {
            // In a real app, we'd fetch from repository which might call Firestore then Room
            // For now, we assume data is seeded in Room
            // Actually, we can just use the parcelable passed, but fetching ensures latest data
        }
    }
}
