package com.raulp.quizgame.ui.rankings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.raulp.quizgame.Response
import com.raulp.quizgame.databinding.FragmentRankingsBinding
import com.raulp.quizgame.repository.GameRepository

/**
 * @author Raul Palade
 * @date 19/12/2022
 * @project QuizGame
 */

class RankingFragment : Fragment() {
    private lateinit var binding: FragmentRankingsBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: RankingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.rankingViewModel = viewModel
        val adapter = RankingListAdapter()
        binding.recyclerView.adapter = adapter

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

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            RankingViewModelFactory(gameRepository)
        )[RankingViewModel::class.java]
    }
}