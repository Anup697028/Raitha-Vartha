package com.raitha.vartha.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.vartha.data.repository.CropRepository
import com.raitha.vartha.models.Crop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CropsViewModel(
    private val repository: CropRepository,
    private val prefManager: com.raitha.vartha.data.local.PreferenceManager
) : ViewModel() {

    private val _allCrops = MutableStateFlow<List<Crop>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val filteredCrops: StateFlow<List<Crop>> = combine(
        _allCrops, _searchQuery, _selectedCategory, prefManager.languageFlow
    ) { crops, query, category, language ->
        crops.filter { crop ->
            (category == "All" || crop.category == category) &&
            (crop.name.contains(query, ignoreCase = true) || crop.nameKn.contains(query))
        }.map { crop ->
            if (language == "kn") {
                crop.copy(
                    name = crop.nameKn,
                    category = crop.categoryKn,
                    overview = crop.overviewKn
                )
            } else {
                crop
            }
        }
    }.onEach { _isLoading.value = false }
     .flowOn(Dispatchers.Default)
     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _isLoading.value = true
            repository.seedCropsIfEmpty()
            repository.allCrops.collect {
                _allCrops.value = it
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }
}
