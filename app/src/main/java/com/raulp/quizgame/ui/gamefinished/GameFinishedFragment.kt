package com.raulp.quizgame.ui.gamefinished

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.raulp.quizgame.Response
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.gameFinishedViewModel = viewModel
        val args: GameFinishedFragmentArgs by navArgs()
        val game = args.game

        val adapter = RankingListAdapter()
        binding.recyclerView.adapter = adapter

        setGameStats(game)

        viewModel.userRanking.observe(viewLifecycleOwner) { userRanking ->
            binding.playerRanking.text = (userRanking + 1).toString()
        }

        viewModel.generalRankings.observe(viewLifecycleOwner) { rankings ->
            when (rankings) {
                is Response.Success -> {
                    binding.totalPlayers.text = rankings.data.size.toString()
                    rankings.let { adapter.submitList(it.data) }
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    @SuppressLint("SetTextI18n")
    private fun setGameStats(game: Game) {
        binding.numberOfQuestions.text = game.totalQuestions.toString()
        binding.totalCorrect.text = game.correct.toString()
        binding.totalWrong.text = (game.wrong + game.notAnswered).toString()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameFinishedViewModelFactory(gameRepository)
        )[GameFinishedViewModel::class.java]
    }
}