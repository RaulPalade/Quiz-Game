package com.raulp.quizgame.repository

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
    private val storage = FirebaseStorage.getInstance()

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

    override suspend fun signUp(email: String, password: String): Response<String> {
        val response = auth.createUserWithEmailAndPassword(email, password).await()
        return if (response.user != null) {
            val id = response.user!!.uid
            Response.Success(id)
        } else {
            Response.Failure("Errore durante il login")
        }
    }

    override suspend fun addUserOnFirestore(user: User, profileImage: Uri): Response<Boolean> {
        val doc = usersRef.document(user.id).get().await()
        return if (!doc.exists()) {

            val profilePhotoRef = storage.getReference("profile_images/${user.id}")
            profilePhotoRef.putFile(profileImage).await()

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