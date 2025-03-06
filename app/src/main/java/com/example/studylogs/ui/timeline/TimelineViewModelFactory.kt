package com.example.studylogs.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studylogs.data.StudyDao

class TimelineViewModelFactory(private val studyDao: StudyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimelineViewModel(studyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}