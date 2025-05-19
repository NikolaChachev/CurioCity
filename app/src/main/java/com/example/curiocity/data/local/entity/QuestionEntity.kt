package com.example.curiocity.data.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionEntity(
    val a: String = "",
    val b: String = "",
    val c: String = "",
    val d: String = "",
    val answer: String = "",
    val question: String = ""
) : Parcelable
