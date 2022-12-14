package com.raulp.quizgame.repository

import com.google.firebase.auth.AuthCredential
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.User

/**
 * @author Raul Palade
 * @date 13/12/2022
 * @project QuizGame
 */

interface IAuthRepository {
    suspend fun signInWithGoogle(googleAuthCredential: AuthCredential): Response<User>

    suspend fun signIn(email: String, password: String): Response<User>

    suspend fun signUp(name: String, email: String, password: String): Response<User>

    suspend fun addUserOnFirestore(user: User): Response<Boolean>

    suspend fun sendVerificationEmail(): Response<Boolean>

    suspend fun resetPassword(email: String): Response<Boolean>
}