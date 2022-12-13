package com.raulp.quizgame.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raulp.quizgame.Response
import com.raulp.quizgame.repository.AuthRepository

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private lateinit var auth: FirebaseAuth

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private var _loginStatus = MutableLiveData<Response<Boolean>>()
    val loginStatus: LiveData<Response<Boolean>>
        get() = _loginStatus

    fun checkIfUserLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        return user != null
    }

    fun logOut() {
        Firebase.auth.signOut();
    }

    fun signIn() {
        val email = email.value.toString()
        val password = password.value.toString()

        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            _loginStatus.postValue(Response.Success(true))
        }.addOnFailureListener {
            _loginStatus.postValue(Response.Failure("Error during login"))
        }
    }

    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        auth = Firebase.auth
        auth.signInWithCredential(googleAuthCredential).addOnSuccessListener {
            _loginStatus.postValue(Response.Success(true))
        }.addOnFailureListener {
            _loginStatus.postValue(Response.Failure("Error during login"))
        }
    }
}