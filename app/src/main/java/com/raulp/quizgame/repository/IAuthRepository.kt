package com.raulp.quizgame.repository

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.data.User

interface IAuthRepository {
    suspend fun signInWithGoogle(googleAuthCredential: AuthCredential): Response<User>

    suspend fun signIn(email: String, password: String): Response<User>

    suspend fun signOut(): Response<Boolean>

    suspend fun signUp(email: String, password: String): Response<String>

    suspend fun addUserOnFirestore(user: User, profileImage: Uri?): Response<Boolean>

    suspend fun sendVerificationEmail(): Response<Boolean>

    suspend fun resetPassword(email: String): Response<Boolean>
}