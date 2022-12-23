package com.raulp.quizgame.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.repository.AuthRepository
import com.raulp.quizgame.repository.GameRepository

/**
 * @author Raul Palade
 * @date 15/12/2022
 * @project QuizGame
 */

class GameViewModelFactory(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            GameViewModel(gameRepository, authRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}