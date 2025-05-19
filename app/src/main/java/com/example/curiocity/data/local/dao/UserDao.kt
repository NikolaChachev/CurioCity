package com.example.curiocity.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.curiocity.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET currentScore = :score WHERE id = :userId")
    suspend fun updateUserScore(userId: Long, score: Int)

    @Query("UPDATE users SET currentLevel = :level WHERE id = :userId")
    suspend fun updateUserLevel(userId: Long, level: Int)

    @Query("UPDATE users SET currentQuestion = :question WHERE id = :userId")
    suspend fun updateUserQuestion(userId: Long, question: Int)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String)
} 