package com.raulp.quizgame.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.User
import kotlinx.coroutines.tasks.await

/**
 * @author Raul Palade
 * @date 13/12/2022
 * @project QuizGame
 */

class AuthRepository : IAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseFirestore.getInstance()
    private val usersRef = rootRef.collection("users")

    override suspend fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): Response<User> {
        val response = auth.signInWithCredential(googleAuthCredential).await()
        return if (response.user != null) {
            val user =
                User(response.user!!.displayName.toString(), response.user!!.email.toString())
            Response.Success(user)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun signIn(email: String, password: String): Response<User> {
        val response = auth.signInWithEmailAndPassword(email, password).await()
        return if (response.user != null) {
            val user =
                User(response.user!!.displayName.toString(), response.user!!.email.toString())
            Response.Success(user)
        } else {
            Response.Failure("Errore durante il login")
        }
    }
}