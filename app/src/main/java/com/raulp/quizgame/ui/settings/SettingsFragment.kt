package com.raulp.quizgame.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.raulp.quizgame.databinding.FragmentSettingsBinding
import com.raulp.quizgame.repository.GameRepository
import com.raulp.quizgame.ui.game.GameViewModel
import com.raulp.quizgame.ui.game.GameViewModelFactory

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.gameViewModel = viewModel

        binding.imageButton.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToMenuFragment()
            this.findNavController().navigate(action)
        }

        binding.profileImage.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
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