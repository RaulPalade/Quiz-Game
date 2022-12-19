package com.raulp.quizgame.repository

import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.data.User

/**
 * @author Raul Palade
 * @date 15/12/2022
 * @project QuizGame
 */

interface IGameRepository {
    suspend fun getQuestions(topic: Topic): Response<List<Question>>

    suspend fun updateUserScore(points: Int): Response<Boolean>

    suspend fun getUserProfile(): Response<User>

    suspend fun getUsersRanking(): Response<List<User>>
}