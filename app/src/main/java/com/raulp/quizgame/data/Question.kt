package com.raulp.quizgame.data

import java.io.Serializable

data class Question(
    val question: String,
    val correct: String,
    val wrongAnswers: ArrayList<*>
) : Serializable
