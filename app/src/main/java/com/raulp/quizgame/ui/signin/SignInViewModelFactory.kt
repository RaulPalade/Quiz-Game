package com.raulp.quizgame.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.repository.AuthRepository

/**
 * @author Raul Palade
 * @date 13/12/2022
 * @project QuizGame
 */

class SignInViewModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            SignInViewModel(authRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}