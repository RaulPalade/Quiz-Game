package com.raulp.quizgame.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.raulp.quizgame.Response
import com.raulp.quizgame.ResponseState
import com.raulp.quizgame.data.User

/**
 * @author Raul Palade
 * @date 13/12/2022
 * @project QuizGame
 */

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseFirestore.getInstance()
    private val usersRef = rootRef.collection("users")
    private var logged = false;

    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()

        println("REPOSITORY")

        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val firebaseUser: FirebaseUser? = auth.currentUser
                if (firebaseUser != null) {
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val user = User(name.toString(), email.toString())
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                authenticatedUserMutableLiveData.value = authTask.exception?.message?.let {
                    ResponseState.Error(it)
                }
            }
        }
        return authenticatedUserMutableLiveData
    }

    fun signIn(email: String, password: String): Response<Boolean> {
        println("XX")
        println(auth.signInWithEmailAndPassword(email, password).isSuccessful)
        return if (auth.signInWithEmailAndPassword(email, password).isComplete) {
            println("YY")
            Response.Success(true)
        } else {
            Response.Failure("ERRORE NEL LOGIN")
        }
    }
}