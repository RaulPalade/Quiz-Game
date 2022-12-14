package com.raulp.quizgame.ui.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.raulp.quizgame.R
import com.raulp.quizgame.Response
import com.raulp.quizgame.databinding.FragmentForgotPasswordBinding
import com.raulp.quizgame.repository.AuthRepository

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val authRepository = AuthRepository()
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.forgotPasswordViewModel = viewModel

        viewModel.resetStatus.observe(viewLifecycleOwner) { resetStatus ->
            when (resetStatus) {
                is Response.Success -> {
                    successSnackbar("Check out your email inbox")
                    val action =
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment()
                    this.findNavController().navigate(action)
                }
                is Response.Failure -> {
                    errorSnackbar("Email does not exist")
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ForgotPasswordViewModelFactory(authRepository)
        )[ForgotPasswordViewModel::class.java]
    }


    @Suppress("SameParameterValue")
    private fun successSnackbar(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(
            ContextCompat.getColor(
                this.requireContext(),
                R.color.green
            )
        ).setTextColor(
            ContextCompat.getColor(
                this.requireContext(),
                R.color.black
            )
        ).show()
    }

    private fun errorSnackbar(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
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
    }
}