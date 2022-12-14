package com.raulp.quizgame.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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

    override suspend fun signInWithGoogle(googleAuthCredential: AuthCredential): Response<Boolean> {
        val response = auth.signInWithCredential(googleAuthCredential).await()
        return if (response.user != null) {
            val user =
                User(response.user!!.displayName.toString(), response.user!!.email.toString())
            Response.Success(true)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun signIn(email: String, password: String): Response<Boolean> {
        val response = auth.signInWithEmailAndPassword(email, password).await()
        return if (response.user != null) {
            Response.Success(true)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Response<FirebaseUser?> {
        val response = auth.createUserWithEmailAndPassword(email, password).await()
        return if (response.user != null) {
            Response.Success(response.user)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun addUserOnFirestore(id: String, user: User): Response<Boolean> {
        val doc = usersRef.document(id).get().await()
        return if (!doc.exists()) {
            usersRef.document(id).set(user)
            Response.Success(true)
        } else {
            Response.Failure("Document already exists")
        }
    }

    override suspend fun sendVerificationEmail(): Response<Boolean> {
        val user = Firebase.auth.currentUser
        return if (user != null) {
            user.sendEmailVerification().await()
            Response.Success(true)
        } else {
            Response.Failure("User does not exists")
        }
    }
}