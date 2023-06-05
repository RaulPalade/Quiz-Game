package com.raulp.quizgame.repository

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.data.User
import kotlinx.coroutines.tasks.await

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
            val profileImage = response.user!!.photoUrl.toString()
            val user =
                User(
                    id = id,
                    email = email.toString(),
                    name = name!![0],
                    profileImage = profileImage
                )
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

    override suspend fun signOut(): Response<Boolean> {
        return try {
            auth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure("Impossible to logout")
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

    override suspend fun addUserOnFirestore(user: User, profileImage: Uri?): Response<Boolean> {
        val doc = usersRef.document(user.id).get().await()
        return if (!doc.exists()) {
            if (profileImage != null) {
                val profilePhotoRef = storage.getReference("profile_images/${user.name}_${user.id}")
                val response =
                    profilePhotoRef.putFile(profileImage).await().storage.downloadUrl.await()
                val newUser =
                    User(name = user.name, email = user.email, profileImage = response.toString())
                usersRef.document(user.id).set(newUser)
            } else {
                val newUser =
                    User(name = user.name, email = user.email, profileImage = user.profileImage)
                usersRef.document(user.id).set(newUser)
            }

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