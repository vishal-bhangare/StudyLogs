package com.example.studylogs.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studylogs.data.StudyDao

class HomeViewModelFactory(private val studyDao: StudyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(studyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
