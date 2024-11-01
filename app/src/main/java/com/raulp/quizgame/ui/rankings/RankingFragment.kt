package com.raulp.quizgame.ui.rankings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.databinding.FragmentRankingsBinding
import com.raulp.quizgame.repository.AuthRepository
import com.raulp.quizgame.repository.GameRepository
import com.raulp.quizgame.ui.game.GameViewModel
import com.raulp.quizgame.ui.game.GameViewModelFactory
import com.squareup.picasso.Picasso

class RankingFragment : Fragment() {
    private lateinit var binding: FragmentRankingsBinding
    private val gameRepository = GameRepository()
    private val authRepository = AuthRepository()
    private lateinit var viewModel: GameViewModel

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
        binding.gameViewModel = viewModel
        val adapter = RankingListAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.getUserProfile()

        viewModel.profile.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Response.Success -> {
                    Picasso.get().load(user.data.profileImage).into(binding.profileImage)
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }


        binding.menuBtn.setOnClickListener {
            val action = RankingFragmentDirections.actionRankingFragmentToMenuFragment()
            this.findNavController().navigate(action)
        }

        binding.profileImage.setOnClickListener {
            val action = RankingFragmentDirections.actionRankingFragmentToProfileFragment()
            this.findNavController().navigate(action)
        }

        viewModel.getRankingList()

        viewModel.personalRanking.observe(viewLifecycleOwner) { userRanking ->
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
            GameViewModelFactory(gameRepository, authRepository)
        )[GameViewModel::class.java]
    }
}