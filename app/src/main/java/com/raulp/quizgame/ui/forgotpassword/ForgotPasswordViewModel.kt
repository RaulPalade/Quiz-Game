package com.raulp.quizgame.ui.forgotpassword

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

class ForgotPasswordViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    var email = MutableLiveData<String>()

    private var _navigateToSignIn = MutableLiveData<Boolean>()
    val navigateToSignIn: LiveData<Boolean>
        get() = _navigateToSignIn

    private var _showSnackbarEventEmail = MutableLiveData<Boolean>()
    val showSnackbarEventEmail: LiveData<Boolean>
        get() = _showSnackbarEventEmail

    fun restorePassword() {
        val email = email.value.toString()

        auth = Firebase.auth
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _navigateToSignIn.value = true
            }.addOnFailureListener {
                _showSnackbarEventEmail.value = true
            }
    }

    fun doneNavigationToLogin() {
        _navigateToSignIn.value = false;
    }

    fun doneShowSnackbarEmail() {
        _showSnackbarEventEmail.value = false
    }
}