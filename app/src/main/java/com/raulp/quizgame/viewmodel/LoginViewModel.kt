package com.raulp.quizgame.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

class LoginViewModel : ViewModel() {
    fun signIn(auth: FirebaseAuth, email: String, password: String) {
        println("ARRIVED HERE")
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                println("LOGGED")
            }
            .addOnFailureListener {
                println(it.message)
            }
    }
}