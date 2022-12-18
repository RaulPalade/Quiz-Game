package com.raulp.quizgame.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
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

    override suspend fun signInWithGoogle(googleAuthCredential: AuthCredential): Response<User> {
        val response = auth.signInWithCredential(googleAuthCredential).await()
        return if (response.user != null) {
            val id = response.user!!.uid
            val email = response.user!!.email
            val name = response.user!!.displayName?.trim()?.split("\\s+".toRegex())
            val user =
                User(id, email.toString(), name!![0])
            Response.Success(user)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun signIn(email: String, password: String): Response<User> {
        val response = auth.signInWithEmailAndPassword(email, password).await()
        return if (response.user != null) {
            val id = response.user!!.uid
            val name = response.user!!.displayName
            val user =
                User(id, email, name.toString())
            Response.Success(user)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Response<User> {
        val response = auth.createUserWithEmailAndPassword(email, password).await()
        return if (response.user != null) {
            val id = response.user!!.uid
            val user = User(id = id, email = email, name = name)
            Response.Success(user)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun addUserOnFirestore(user: User): Response<Boolean> {
        val doc = usersRef.document(user.id).get().await()
        return if (!doc.exists()) {
            val newUser = User(user.name, user.email)
            usersRef.document(user.id).set(newUser)
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

    override suspend fun resetPassword(email: String): Response<Boolean> {
        val response = auth.fetchSignInMethodsForEmail(email).await()
        return if (response.signInMethods != null) {
            auth.sendPasswordResetEmail(email).await()
            Response.Success(true)
        } else {
            Response.Failure("User does not exists")
        }
    }
}