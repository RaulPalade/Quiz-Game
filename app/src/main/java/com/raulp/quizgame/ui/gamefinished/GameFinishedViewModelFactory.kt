package com.raulp.quizgame.ui.gamefinished

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.repository.GameRepository

/**
 * @author Raul Palade
 * @date 17/12/2022
 * @project QuizGame
 */

class GameFinishedViewModelFactory(private val gameRepository: GameRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GameFinishedViewModel::class.java)) {
            GameFinishedViewModel(gameRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}