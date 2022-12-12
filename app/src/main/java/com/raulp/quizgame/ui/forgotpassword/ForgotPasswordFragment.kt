package com.raulp.quizgame.ui.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.raulp.quizgame.R
import com.raulp.quizgame.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.forgotPasswordViewModel = viewModel

        viewModel.navigateToSignIn.observe(viewLifecycleOwner) {
            if (it == true) {
                val action =
                    ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment()
                this.findNavController().navigate(action)
                viewModel.doneNavigationToLogin()
            }
        }

        viewModel.showSnackbarEventEmail.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Email not valid",
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(
                    ContextCompat.getColor(
                        this.requireContext(),
                        R.color.orange
                    )
                ).setTextColor(
                    ContextCompat.getColor(
                        this.requireContext(),
                        R.color.white
                    )
                ).show()
                viewModel.doneShowSnackbarEmail()
            }
        }
    }
}