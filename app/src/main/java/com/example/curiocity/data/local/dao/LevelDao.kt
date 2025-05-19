package com.example.curiocity.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.curiocity.data.local.entity.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels")
    fun getAllLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM levels WHERE id = :levelId")
    fun getLevelById(levelId: Long): Flow<LevelEntity?>

    @Query("SELECT * FROM levels WHERE levelNumber = :level")
    fun getLevelByNumber(level: Int): LevelEntity?

    @Query("SELECT * FROM levels WHERE name = :levelName")
    fun getLevelByName(levelName: String): Flow<LevelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevel(level: LevelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Update
    suspend fun updateLevel(level: LevelEntity)

    @Delete
    suspend fun deleteLevel(level: LevelEntity)

    @Query("DELETE FROM levels")
    suspend fun deleteAllLevels()
} 