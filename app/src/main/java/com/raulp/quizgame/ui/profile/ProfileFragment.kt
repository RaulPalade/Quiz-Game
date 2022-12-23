package com.raulp.quizgame.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.raulp.quizgame.AuthActivity
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.databinding.FragmentProfileBinding
import com.raulp.quizgame.repository.AuthRepository
import com.raulp.quizgame.repository.GameRepository
import com.raulp.quizgame.ui.game.GameViewModel
import com.raulp.quizgame.ui.game.GameViewModelFactory
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val gameRepository = GameRepository()
    private val authRepository = AuthRepository()
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.gameViewModel = viewModel

        viewModel.getUserProfile()

        viewModel.profile.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Response.Success -> {
                    Picasso.get().load(user.data.profileImage).into(binding.profileImage)
                    binding.userName.text = user.data.name
                    binding.playerPoints.text = user.data.score.toString()
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }

        viewModel.getRankingList()

        viewModel.personalRanking.observe(viewLifecycleOwner) { userRanking ->
            binding.playerRanking.text = (userRanking + 1).toString()
        }

        viewModel.generalRankings.observe(viewLifecycleOwner) { rankings ->
            when (rankings) {
                is Response.Success -> {
                    binding.totalPlayers.text = rankings.data.size.toString()
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }

        viewModel.logout.observe(viewLifecycleOwner) { logout ->
            when (logout) {
                is Response.Success -> {
                    goToSignIn()
                }
                is Response.Failure -> {
                    println("Unable to logout")
                }
            }
        }

        binding.menuBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToMenuFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun goToSignIn() {
        val intent = Intent(context, AuthActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(gameRepository, authRepository)
        )[GameViewModel::class.java]
    }
}