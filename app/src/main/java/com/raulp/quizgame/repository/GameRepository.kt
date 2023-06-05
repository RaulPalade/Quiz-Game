package com.raulp.quizgame.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.data.Topic.*
import com.raulp.quizgame.data.User
import kotlinx.coroutines.tasks.await

class GameRepository : IGameRepository {
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseFirestore.getInstance()
    private val userRef = rootRef.collection("users")
    private val americasRef = rootRef.collection("questions_americas")
    private val europeAfricaRef = rootRef.collection("questions_europe_africa")
    private val asiaOceaniaRef = rootRef.collection("questions_asia_oceania")

    override suspend fun getUserProfile(): Response<User> {
        try {
            val userId = auth.currentUser?.uid
            val docSnap = userId?.let { userRef.document(it).get().await() }

            val name = docSnap?.data?.get("name").toString()
            val email = docSnap?.data?.get("email").toString()
            val score = Integer.parseInt(docSnap?.data?.get("score").toString())
            val profileImage = docSnap?.data?.get("profileImage").toString()

            val user = User(
                name = name,
                email = email,
                score = score,
                profileImage = profileImage
            )

            return Response.Success(user)
        } catch (e: Exception) {
            return Response.Failure("User not found")
        }
    }

    override suspend fun getQuestionList(topic: Topic): Response<List<Question>> {
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

    override suspend fun getRankingList(): Response<List<User>> {
        try {
            val response = userRef.get().await()
            val users = ArrayList<User>()

            response.documents.forEach { docSnap ->
                val name = docSnap.data?.get("name").toString()
                val email = docSnap.data?.get("email").toString()
                val score = Integer.parseInt(docSnap.data?.get("score").toString())
                val profileImage = docSnap.data?.get("profileImage").toString()

                val user =
                    User(name = name, email = email, score = score, profileImage = profileImage)
                if (auth.currentUser?.uid == docSnap.id) {
                    user.id = auth.currentUser!!.uid
                }
                users.add(user)
            }

            users.sortByDescending { user -> user.score }
            return Response.Success(users)
        } catch (e: Exception) {
            return Response.Failure("Impossible to get users ranking")
        }
    }

    override suspend fun updateUserScore(points: Int): Response<Boolean> {
        return try {
            val response = auth.currentUser?.let { userRef.document(it.uid).get().await() }
            val oldScore = Integer.parseInt(response?.data?.get("score").toString())
            val newScore = oldScore + points
            auth.currentUser?.let { userRef.document(it.uid).update("score", newScore).await() }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure("Impossible to update document")
        }
    }
}