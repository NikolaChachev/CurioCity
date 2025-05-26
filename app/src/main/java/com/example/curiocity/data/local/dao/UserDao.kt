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

    @Query("UPDATE users SET currentScore = :score WHERE uuid = :userId")
    suspend fun updateUserScore(userId: String, score: Int)

    @Query("UPDATE users SET currentLevel = :level WHERE uuid = :userId")
    suspend fun updateUserLevel(userId: String, level: Int)

    @Query("UPDATE users SET currentQuestion = :question WHERE uuid = :userId")
    suspend fun updateUserQuestion(userId: String, question: Int)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String)

    @Query("UPDATE users SET lives = :lives WHERE uuid = :userId")
    suspend fun updateUserLives(lives: Int, userId: String)
} 