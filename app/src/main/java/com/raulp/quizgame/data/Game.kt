package com.raulp.quizgame.data

import java.io.Serializable

data class Game(
    var totalQuestions: Int,
    var correct: Int = 0,
    var wrong: Int = 0,
    var notAnswered: Int = 0,
    var points: Int = 0
) : Serializable