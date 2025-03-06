package com.example.studylogs.ui.timeline


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studylogs.data.StudyDao
import com.example.studylogs.data.StudySession
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class TimelineViewModel(private val studyDao: StudyDao) : ViewModel() {
    private val _sessions = MutableLiveData<List<StudySession>>()
    val sessions: LiveData<List<StudySession>> = _sessions

    private val _currentDate = MutableLiveData<Date>()
    val currentDate: LiveData<Date> = _currentDate

    init {
        _currentDate.value = Date()
        loadSessionsForCurrentDate()
    }
    fun isCurrentDate(): Boolean {
        val calendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance().apply {
            time = _currentDate.value ?: Date()
        }

        return calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun loadSessionsForCurrentDate() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                time = _currentDate.value ?: Date()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val startOfDay = calendar.timeInMillis
            val endOfDay = startOfDay + TimeUnit.DAYS.toMillis(1)

            studyDao.getSessionsByDay(startOfDay, endOfDay).collect { sessionList ->
                _sessions.value = sessionList
            }
        }
    }

    fun formatDate(date: Date): String {
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        return "${dayFormat.format(date)}, ${dateFormat.format(date)}"
    }

    fun navigateDate(forward: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.time = _currentDate.value ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, if (forward) 1 else -1)
        _currentDate.value = calendar.time
        loadSessionsForCurrentDate()
    }
}