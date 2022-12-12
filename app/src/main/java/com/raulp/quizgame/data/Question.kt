package com.raulp.quizgame.data

data class Question(
    val question: String,
    val answer: String,
    val wrongAnswers: ArrayList<String>
)
