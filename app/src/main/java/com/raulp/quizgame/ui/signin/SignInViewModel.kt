package com.raulp.quizgame.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

class SignInViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private var _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackbarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

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
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _navigateToHome.value = true
            }
            .addOnFailureListener {
                _showSnackbarEvent.value = true
            }
    }

    fun doneNavigationToHome() {
        _navigateToHome.value = false;
    }

    fun doneShowSnackbar() {
        _showSnackbarEvent.value = false
    }
}