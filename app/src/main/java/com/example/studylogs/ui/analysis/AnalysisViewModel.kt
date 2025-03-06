package com.example.studylogs.ui.analysis


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studylogs.data.StudyDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class AnalysisViewModel(private val studyDao: StudyDao) : ViewModel() {
    private val _totalStudyTime = MutableLiveData<Long>()
    val totalStudyTime: LiveData<Long> = _totalStudyTime

    private val _tagDistribution = MutableLiveData<Map<String, Long>>()
    val tagDistribution: LiveData<Map<String, Long>> = _tagDistribution

    private val _dailyAverage = MutableLiveData<Long>()
    val dailyAverage: LiveData<Long> = _dailyAverage

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -7) // Last 7 days
            val startTime = calendar.timeInMillis

            studyDao.getSessionsByDay(startTime, System.currentTimeMillis()).collect { sessions ->
                // Calculate total study time
                val total = sessions.sumOf { session ->
                    if (session.endTime != null) {
                        session.endTime - session.startTime
                    } else {
                        (session.duration ?: 0) * 60 * 1000L
                    }
                }
                _totalStudyTime.value = TimeUnit.MILLISECONDS.toMinutes(total)

                // Calculate tag distribution
                val tagMap = sessions.groupBy { it.tag }
                    .mapValues { entry ->
                        entry.value.sumOf { session ->
                            if (session.endTime != null) {
                                TimeUnit.MILLISECONDS.toMinutes(session.endTime - session.startTime)
                            } else {
                                session.duration?.toLong() ?: 0L
                            }
                        }
                    }
                _tagDistribution.value = tagMap

                // Calculate daily average
                _dailyAverage.value = _totalStudyTime.value?.div(7)
            }
        }
    }
}