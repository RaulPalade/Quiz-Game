package com.raulp.quizgame.repository

import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Topic

/**
 * @author Raul Palade
 * @date 15/12/2022
 * @project QuizGame
 */

interface IGameRepository {
    suspend fun getQuestions(topic: Topic): Response<List<Question>>
}