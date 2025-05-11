package com.example.curiocity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questions: List<QuestionEntity> = emptyList(),
    val name: String = "",
    @get:Exclude
    val levelNumber: Int = 1
)
