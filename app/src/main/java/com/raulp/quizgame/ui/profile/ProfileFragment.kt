package com.raulp.quizgame.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.raulp.quizgame.databinding.FragmentProfileBinding
import com.raulp.quizgame.repository.GameRepository

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.profileViewModel = viewModel
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(gameRepository)
        )[ProfileViewModel::class.java]
    }
}