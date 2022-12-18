package com.raulp.quizgame.ui.gamefinished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.raulp.quizgame.data.Game
import com.raulp.quizgame.databinding.FragmentGameFinishedBinding
import com.raulp.quizgame.repository.GameRepository

class GameFinishedFragment : Fragment() {
    private lateinit var binding: FragmentGameFinishedBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: GameFinishedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.gameFinishedViewModel = viewModel
        val args: GameFinishedFragmentArgs by navArgs()
        val game = args.game

        setGameStats(game)

        viewModel.getUsersRanking()
    }

    private fun setGameStats(game: Game) {
        binding.totalAnswered.text = game.totalQuestions.toString()
        binding.totalCorrect.text = game.correct.toString()
        binding.totalWrong.text = game.wrong.toString()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameFinishedViewModelFactory(gameRepository)
        )[GameFinishedViewModel::class.java]
    }
}