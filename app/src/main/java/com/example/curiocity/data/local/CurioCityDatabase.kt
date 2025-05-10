package com.example.curiocity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.curiocity.data.local.converter.QuestionListConverter
import com.example.curiocity.data.local.dao.LevelDao
import com.example.curiocity.data.local.dao.UserDao
import com.example.curiocity.data.local.entity.LevelEntity
import com.example.curiocity.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, LevelEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(QuestionListConverter::class)
abstract class CurioCityDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun levelDao(): LevelDao

    companion object {
        const val DATABASE_NAME = "curiocity_database"
    }
} 