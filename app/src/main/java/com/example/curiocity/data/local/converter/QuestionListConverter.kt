package com.example.curiocity.data.local.converter

import androidx.room.TypeConverter
import com.example.curiocity.data.local.entity.QuestionEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuestionListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromQuestionList(questions: List<QuestionEntity>?): String? {
        return questions?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toQuestionList(questionsString: String?): List<QuestionEntity>? {
        if (questionsString == null) return null
        val listType = object : TypeToken<List<QuestionEntity>>() {}.type
        return gson.fromJson(questionsString, listType)
    }
} 