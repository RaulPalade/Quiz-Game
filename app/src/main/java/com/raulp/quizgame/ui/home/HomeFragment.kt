package com.raulp.quizgame.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        Picasso.get()
            .load("https://firebasestorage.googleapis.com/v0/b/quiz-game-4c85c.appspot.com/o/profile_images%2Faiony-haust-3TLl_97HNJo-unsplash.jpg?alt=media&token=c7823b9a-be22-4cc2-b198-ede2a44de904")
            .into(binding.profileImage)

        binding.profileImage.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            this.findNavController().navigate(action)
        }

        binding.card1.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(EUROPE_AFRICA)
            this.findNavController().navigate(action)
        }

        binding.card2.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(ASIA_OCEANIA)
            this.findNavController().navigate(action)
        }

        binding.card3.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(AMERICAS)
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