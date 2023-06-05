package com.raulp.quizgame.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.repository.AuthRepository

class ForgotPasswordViewModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            ForgotPasswordViewModel(authRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}