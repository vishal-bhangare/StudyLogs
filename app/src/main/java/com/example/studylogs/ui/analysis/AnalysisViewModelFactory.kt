package com.example.studylogs.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studylogs.data.StudyDao

class AnalysisViewModelFactory(private val studyDao: StudyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalysisViewModel(studyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}