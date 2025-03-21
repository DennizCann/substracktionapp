package com.denizcan.substracktionapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.denizcan.substracktionapp.data.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _currentLanguage = MutableStateFlow("tr")
    val currentLanguage: StateFlow<String> = _currentLanguage

    init {
        viewModelScope.launch {
            dataStoreRepository.getSelectedLanguage().collectLatest { language ->
                _currentLanguage.value = language
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            dataStoreRepository.saveLanguage(language)
            _currentLanguage.value = language
        }
    }
}

class LanguageViewModelFactory(
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LanguageViewModel(dataStoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 