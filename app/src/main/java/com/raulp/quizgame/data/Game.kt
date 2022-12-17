package com.raulp.quizgame.data

import java.io.Serializable

/**
 * @author Raul Palade
 * @date 17/12/2022
 * @project QuizGame
 */

data class Game(
    var totalQuestions: Int,
    var correct: Int = 0,
    var wrong: Int = 0,
    var points: Int = 0
) : Serializable