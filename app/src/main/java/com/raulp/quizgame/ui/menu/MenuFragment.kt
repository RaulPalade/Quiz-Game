package com.raulp.quizgame.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.raulp.quizgame.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButton.setOnClickListener { this.findNavController().popBackStack() }

        binding.homeBtn.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToHomeFragment()
            this.findNavController().navigate(action)
        }

        binding.profileBtn.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToProfileFragment()
            this.findNavController().navigate(action)
        }

        binding.rankingBtn.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToRankingFragment()
            this.findNavController().navigate(action)
        }

        binding.settingsBtn.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToSettingsFragment()
            this.findNavController().navigate(action)
        }
    }
}