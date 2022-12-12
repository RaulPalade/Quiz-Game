package com.raulp.quizgame.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.raulp.quizgame.MainActivity
import com.raulp.quizgame.R
import com.raulp.quizgame.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private val viewModel: SignInViewModel by viewModels()

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.signInViewModel = viewModel

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it == true) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
                viewModel.doneNavigationToHome()
            }
        }

        viewModel.showSnackbarEvent.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Credentials are incorrect",
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
                viewModel.doneShowSnackbar()
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment()
            this.findNavController().navigate(action)
        }

        binding.btnRegister.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            this.findNavController().navigate(action)
        }
    }
}