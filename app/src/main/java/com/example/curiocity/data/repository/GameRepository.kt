package com.example.curiocity.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.curiocity.data.local.SharedPreferencesManager
import com.example.curiocity.data.local.dao.LevelDao
import com.example.curiocity.data.local.dao.UserDao
import com.example.curiocity.data.local.entity.LevelEntity
import com.example.curiocity.data.local.entity.UserEntity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val userDao: UserDao,
    private val levelDao: LevelDao,
    private val firebaseDatabase: FirebaseDatabase,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    lateinit var currentUser: UserEntity
    private lateinit var usersDataList: List<UserEntity>
    private val _currentLives = MutableLiveData<Int>()
    val currentLives: LiveData<Int> = _currentLives
    private val _playerScore = MutableLiveData(0)
    val playerScore: LiveData<Int> = _playerScore

    suspend fun checkForExistingUser(): Boolean {
        val userId = sharedPreferencesManager.getUserUUID() ?: return false
        usersDataList = fetchUsers()
        val user = usersDataList.find { it.uuid == userId } ?: return false
        currentUser = user
        _currentLives.postValue(currentUser.lives)
        _playerScore.postValue(currentUser.currentScore)
        return true
    }

    suspend fun createUser(username: String) = withContext(Dispatchers.IO) {
        val uuid = UUID.randomUUID().toString()
        val user = UserEntity(
            username = username,
            uuid = uuid
        )

        firebaseDatabase.reference
            .child("players")
            .child(uuid)
            .setValue(user)
            .await()
        currentUser = user
        _currentLives.postValue(currentUser.lives)
        _playerScore.postValue(currentUser.currentScore)
        sharedPreferencesManager.saveUserUUID(user.uuid)
    }

    suspend fun fetchUsers(): List<UserEntity> = withContext(Dispatchers.IO) {
        val snapshot = firebaseDatabase.reference.child("players").get().await()
        val data = snapshot.children.map { dataShot ->
            dataShot.getValue(UserEntity::class.java)?.copy(uuid = dataShot.key ?: "")
        }
        val existingUsers = data.requireNoNulls()
        existingUsers.forEach {
            userDao.insertUser(it)
        }
        existingUsers
    }

    suspend fun fetchLevelsData() = withContext(Dispatchers.IO) {
        val snapshot = firebaseDatabase.reference.child("levels").get().await()
        val data = snapshot.children.map { dataSnapshot ->
            dataSnapshot.getValue(LevelEntity::class.java)
                ?.copy(levelNumber = dataSnapshot.key?.toInt() ?: 1)
        }.requireNoNulls()
        levelDao.insertLevels(data)
    }

    suspend fun getCurrentLevelData(level: Int): LevelEntity? = withContext(Dispatchers.IO) {
        levelDao.getLevelByNumber(level)
    }

    suspend fun updateUserScore(score: Int) = withContext(Dispatchers.IO) {
        currentUser = currentUser.copy(currentScore = currentUser.currentScore + score)
        _playerScore.postValue(currentUser.currentScore)
        userDao.updateUserScore(currentUser.uuid, currentUser.currentScore)
        syncUserWithFirebase()
    }

    suspend fun updateUserLevel(level: Int) = withContext(Dispatchers.IO) {
        currentUser = currentUser.copy(currentLevel = level)
        userDao.updateUserLevel(currentUser.uuid, level)
        syncUserWithFirebase()
    }

    suspend fun updateUserQuestion(question: Int) = withContext(Dispatchers.IO) {
        currentUser = currentUser.copy(currentQuestion = question)
        userDao.updateUserQuestion(currentUser.uuid, question)
        syncUserWithFirebase()
    }

    suspend fun updateUserLives(lives: Int) = withContext(Dispatchers.IO) {
        currentUser = currentUser.copy(lives = lives)
        _currentLives.postValue(lives)
        syncUserWithFirebase()
    }

    suspend fun removeLifeFromPlayer() = withContext(Dispatchers.IO) {
        currentUser = currentUser.copy(lives = currentUser.lives - 1)
        _currentLives.postValue(currentUser.lives)

        syncUserWithFirebase()
    }


    private suspend fun syncUserWithFirebase() = withContext(Dispatchers.IO) {
        firebaseDatabase.reference
            .child("players")
            .child(currentUser.uuid)
            .setValue(currentUser)
            .await()
    }

    suspend fun deleteUserByUsername(username: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByUsername(username)
            if (user != null) {
                firebaseDatabase.reference
                    .child("players")
                    .child(user.uuid)
                    .removeValue()
                    .await()

                userDao.deleteUserByUsername(username)

                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error deleting user: ${e.message}")
            false
        }
    }

    fun saveCurrentTime() =
        sharedPreferencesManager.saveLastAppCloseTimestamp(System.currentTimeMillis())


    fun loadLastCloseTimer() = sharedPreferencesManager.getLastAppCloseTimestamp()

    fun saveLifeTimer(timer: Long) = sharedPreferencesManager.saveTimeUntilNextLife(timer)

    fun loadLifeTimer() = sharedPreferencesManager.getTimeUntilNextLife()

} 