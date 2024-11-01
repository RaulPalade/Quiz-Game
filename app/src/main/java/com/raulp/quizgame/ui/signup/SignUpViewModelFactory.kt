package com.raulp.quizgame.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.repository.AuthRepository

class SignUpViewModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            SignUpViewModel(authRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}