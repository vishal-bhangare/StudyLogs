package com.example.studylogs.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long?,
    val duration: Int?, // in minutes, for timer mode
    val tag: String,
    val isTimerMode: Boolean
)