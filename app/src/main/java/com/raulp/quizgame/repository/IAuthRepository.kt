package com.raulp.quizgame.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.User

/**
 * @author Raul Palade
 * @date 13/12/2022
 * @project QuizGame
 */

interface IAuthRepository {
    suspend fun signInWithGoogle(googleAuthCredential: AuthCredential): Response<Boolean>

    suspend fun signIn(email: String, password: String): Response<Boolean>

    suspend fun signUp(name: String, email: String, password: String): Response<FirebaseUser?>

    suspend fun addUserOnFirestore(id: String, user: User): Response<Boolean>

    suspend fun sendVerificationEmail(): Response<Boolean>

    suspend fun resetPassword(email: String): Response<Boolean>
}