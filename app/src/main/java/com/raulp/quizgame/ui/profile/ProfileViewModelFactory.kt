package com.raulp.quizgame.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.repository.GameRepository

/**
 * @author Raul Palade
 * @date 19/12/2022
 * @project QuizGame
 */

class ProfileViewModelFactory(private val gameRepository: GameRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            ProfileViewModel(gameRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}