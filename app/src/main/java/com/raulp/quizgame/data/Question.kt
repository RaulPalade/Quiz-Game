package com.raulp.quizgame.data

import java.io.Serializable

data class Question(
    val question: String,
    val answer: String,
    val wrongAnswers: ArrayList<*>
) : Serializable
