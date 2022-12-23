package com.raulp.quizgame.ui.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.raulp.quizgame.MainActivity
import com.raulp.quizgame.R
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.databinding.FragmentSignUpBinding
import com.raulp.quizgame.repository.AuthRepository

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val authRepository = AuthRepository()
    private lateinit var viewModel: SignUpViewModel
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.signUpViewModel = viewModel

        binding.btnAddPhoto.setOnClickListener {
            openGallery()
        }

        viewModel.registerStatus.observe(viewLifecycleOwner) { registerStatus ->
            when (registerStatus) {
                is Response.Success -> {
                    goToHome()
                }
                is Response.Failure -> {
                    println(registerStatus.message)
                    errorSnackbar(registerStatus.message)
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_PICK
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                imageUri?.let { viewModel.setProfileImage(it) }
            }
        }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            SignUpViewModelFactory(authRepository)
        )[SignUpViewModel::class.java]
    }

    private fun goToHome() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
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