package com.example.curiocity.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class UserEntity(

    val username: String = "",
    val currentScore: Int = 0,
    val currentLevel: Int = 1,
    val currentQuestion: Int = 1,
    @PrimaryKey
    @get:Exclude
    val uuid: String = "",
    val lives: Int = 5
) : Parcelable {

    companion object {
        private const val MAX_LIVES = 5
    }
}