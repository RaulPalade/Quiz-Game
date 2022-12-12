package com.raulp.quizgame.ui.signup

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
import com.raulp.quizgame.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.signUpViewModel = viewModel

        viewModel.navigateToSignIn.observe(viewLifecycleOwner) {
            if (it == true) {
                val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
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

        viewModel.showSnackbarEventPassword.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Minimum passowrd length is 6 characters",
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
                viewModel.doneShowSnackbarPassword()
            }
        }
    }
}