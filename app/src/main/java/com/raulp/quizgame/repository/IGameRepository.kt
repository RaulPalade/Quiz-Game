package com.raulp.quizgame.repository

import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.data.User

interface IGameRepository {
    suspend fun getUserProfile(): Response<User>

    suspend fun getQuestionList(topic: Topic): Response<List<Question>>

    suspend fun getRankingList(): Response<List<User>>

    suspend fun updateUserScore(points: Int): Response<Boolean>
}