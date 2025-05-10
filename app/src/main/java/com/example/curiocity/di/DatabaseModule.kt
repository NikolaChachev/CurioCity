package com.example.curiocity.di

import android.content.Context
import androidx.room.Room
import com.example.curiocity.BuildConfig
import com.example.curiocity.data.local.CurioCityDatabase
import com.example.curiocity.data.local.dao.LevelDao
import com.example.curiocity.data.local.dao.UserDao
import com.example.curiocity.data.repository.GameRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CurioCityDatabase {
        return Room.databaseBuilder(
            context,
            CurioCityDatabase::class.java,
            CurioCityDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: CurioCityDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideLevelDao(database: CurioCityDatabase) = database.levelDao()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(BuildConfig.FIREBASE_URL)
            .apply {
                setPersistenceEnabled(true)
            }
    }

    @Provides
    @Singleton
    fun provideGameRepository(
        userDao: UserDao,
        levelDao: LevelDao,
        firebaseDatabase: FirebaseDatabase
    ): GameRepository {
        return GameRepository(userDao, levelDao, firebaseDatabase)
    }
} 