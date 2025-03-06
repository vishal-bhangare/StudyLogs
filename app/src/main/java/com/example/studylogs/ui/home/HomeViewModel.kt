package com.example.studylogs.ui.home


import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import com.example.studylogs.data.StudyDao
import com.example.studylogs.data.StudySession
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val studyDao: StudyDao) : ViewModel() {
    private var timer: CountDownTimer? = null
    private var currentSession: StudySession? = null

    private val _timeDisplay = MutableLiveData("00:00")
    val timeDisplay: LiveData<String> = _timeDisplay

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _isTimerMode = MutableLiveData(true)
    val isTimerMode: LiveData<Boolean> = _isTimerMode

    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var currentSessionId: Long = 0

    fun toggleMode() {
        _isTimerMode.value = !(_isTimerMode.value ?: true)
        resetTimer()
    }

    fun startTimer(duration: Int? = null) {
        if (_isRunning.value == true) return

        startTime = System.currentTimeMillis()
        _isRunning.value = true

        if (_isTimerMode.value == false && duration != null) {
            startCountDownTimer(duration)
        } else {
            startStopwatch()
        }

        viewModelScope.launch {
            val newSession = StudySession(
                startTime = startTime,
                endTime = null,
                duration = duration,
                tag = "Study",
                isTimerMode = _isTimerMode.value ?: true
            )
            currentSessionId = studyDao.insertSession(newSession)
            currentSession = newSession.copy(id = currentSessionId)
            Log.d("HomeViewModel", "New session created with ID: $currentSessionId")
        }
    }

    private fun startCountDownTimer(duration: Int) {
        timer = object : CountDownTimer(duration * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                _timeDisplay.value = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                stopTimer()
            }
        }.start()
    }

    private fun startStopwatch() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime = System.currentTimeMillis() - startTime
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / 1000 / 60)
                _timeDisplay.value = String.format("%02d:%02d", minutes, seconds)
            }
            override fun onFinish() {}
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
        _isRunning.value = false

        viewModelScope.launch {
            if (currentSessionId > 0) {
                val session = studyDao.getSessionById(currentSessionId)
                session?.let {
                    val endTimeMillis = System.currentTimeMillis()
                    val calculatedDuration = ((endTimeMillis - it.startTime) / 60000).toInt()

                    val updatedSession = it.copy(
                        endTime = endTimeMillis,
                        duration = calculatedDuration
                    )
                    studyDao.updateSession(updatedSession)
                    Log.d("HomeViewModel", "Session updated with ID: $currentSessionId")
                }
            }
        }
        resetTimer()
    }

    private fun resetTimer() {
        timer?.cancel()
        _timeDisplay.value = if (_isTimerMode.value == true) "00:00" else "00:00"
        currentSession = null
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}