package com.example.studylogs.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Insert
    suspend fun insertSession(session: StudySession): Long

    @Update
    suspend fun updateSession(session: StudySession)

    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay ORDER BY startTime DESC")
    fun getSessionsByDay(startOfDay: Long, endOfDay: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): StudySession?
}
