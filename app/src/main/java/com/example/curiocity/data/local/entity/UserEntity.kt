package com.example.curiocity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String = "",
    val currentScore: Int = 0,
    val currentLevel: Int = 1,
    val currentQuestion: Int = 1,
    val uuid: String = ""
) 