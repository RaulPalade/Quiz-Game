package com.raulp.quizgame.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.data.Topic.*
import kotlinx.coroutines.tasks.await

/**
 * @author Raul Palade
 * @date 15/12/2022
 * @project QuizGame
 */

class GameRepository : IGameRepository {
    private val rootRef = FirebaseFirestore.getInstance()
    private val americasRef = rootRef.collection("questions_americas")
    private val europeAfricaRef = rootRef.collection("questions_europe_africa")
    private val asiaOceaniaRef = rootRef.collection("questions_asia_oceania")

    override suspend fun getQuestions(topic: Topic, limit: Int): Response<List<Question>> {
        val response = when (topic) {
            AMERICAS -> {
                americasRef.get().await()
            }
            EUROPE_AFRICA -> {
                europeAfricaRef.get().await()
            }
            ASIA_OCEANIA -> {
                asiaOceaniaRef.get().await()
            }
        }

        return if (response.isEmpty) {
            Response.Failure("No questions were found")
        } else {
            val questions = ArrayList<Question>()
            response.documents.forEach { docSnap ->
                val question = docSnap.data?.get("question")
                val correct = docSnap.data?.get("correct")
                val wrongAnswers = docSnap.data?.get("wrongAnswers")

                val q = Question(
                    question as String,
                    correct as String,
                    wrongAnswers as ArrayList<*>
                )
                questions.add(q)
            }
            Response.Success(questions)
        }
    }
}