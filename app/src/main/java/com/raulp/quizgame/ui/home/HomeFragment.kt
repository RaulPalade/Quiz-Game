package com.raulp.quizgame.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.raulp.quizgame.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.card1.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(1)
            this.findNavController().navigate(action)
        }

        binding.card2.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(2)
            this.findNavController().navigate(action)
        }

        binding.card3.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToGameStartedFragment(3)
            this.findNavController().navigate(action)
        }
    }
}