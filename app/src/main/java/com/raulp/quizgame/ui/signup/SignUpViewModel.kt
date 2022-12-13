package com.raulp.quizgame.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raulp.quizgame.data.User

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

class SignUpViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    var name = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private var _navigateToSignIn = MutableLiveData<Boolean>()
    val navigateToSignIn: LiveData<Boolean>
        get() = _navigateToSignIn

    private var _showSnackbarEventEmail = MutableLiveData<Boolean>()
    val showSnackbarEventEmail: LiveData<Boolean>
        get() = _showSnackbarEventEmail

    private var _showSnackbarEventPassword = MutableLiveData<Boolean>()
    val showSnackbarEventPassword: LiveData<Boolean>
        get() = _showSnackbarEventPassword

    fun signUp() {
        val name = name.value.toString()
        val email = email.value.toString()
        val password = password.value.toString()

        if (password.length < 5) {
            _showSnackbarEventPassword.value = true
            return
        }

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = User(name, email)
                sendVerificationEmail()
                addUserOnFirestore(auth.uid.toString(), user)
            }.addOnFailureListener {
                _showSnackbarEventEmail.value = true
            }
    }

    private fun sendVerificationEmail() {
        val user = Firebase.auth.currentUser
        user!!.sendEmailVerification()
    }

    private fun addUserOnFirestore(id: String, user: User) {
        val db = Firebase.firestore

        db.collection("users").document(id).set(user)
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

    fun doneShowSnackbarPassword() {
        _showSnackbarEventEmail.value = false
    }
}