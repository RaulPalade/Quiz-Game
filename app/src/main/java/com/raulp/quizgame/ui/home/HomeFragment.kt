package com.raulp.quizgame.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Topic.*
import com.raulp.quizgame.databinding.FragmentHomeBinding
import com.raulp.quizgame.repository.GameRepository
import com.raulp.quizgame.ui.game.GameViewModel
import com.raulp.quizgame.ui.game.GameViewModelFactory
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.gameViewModel = viewModel

        viewModel.getUserProfile()

        var photoUrl = ""
        viewModel.user.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Response.Success -> {
                    photoUrl = user.data.profileImage
                    Picasso.get().load(photoUrl).into(binding.profileImage)
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }

        binding.imageButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMenuFragment()
            this.findNavController().navigate(action)
        }

        binding.profileImage.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            this.findNavController().navigate(action)
        }

        binding.card1.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(
                    EUROPE_AFRICA,
                    photoUrl
                )
            this.findNavController().navigate(action)
        }

        binding.card2.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(
                    ASIA_OCEANIA,
                    photoUrl
                )
            this.findNavController().navigate(action)
        }

        binding.card3.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(AMERICAS, photoUrl)
            this.findNavController().navigate(action)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(gameRepository)
        )[GameViewModel::class.java]
    }
}