package com.raulp.quizgame.ui.signin

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.raulp.quizgame.MainActivity
import com.raulp.quizgame.R
import com.raulp.quizgame.Response
import com.raulp.quizgame.databinding.FragmentSignInBinding
import com.raulp.quizgame.repository.AuthRepository
import kotlinx.coroutines.launch


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val authRepository = AuthRepository()
    private lateinit var viewModel: SignInViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        initGoogleSignInClient()

        binding.lifecycleOwner = this
        binding.signInViewModel = viewModel

        viewModel.logOut()

        if (viewModel.checkIfUserLoggedIn()) {
            goToHome()
        }

        binding.btnLogin.setOnClickListener { lifecycleScope.launch { viewModel.signIn() } }

        viewModel.loginStatus.observe(viewLifecycleOwner) { login ->
            when (login) {
                is Response.Success -> {
                    goToHome()
                }
                is Response.Failure -> {
                    makeSnackbar(login.message)
                }
                else -> {}
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

        binding.btnLoginGoogle.setOnClickListener {
            signInUsingGoogle()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            SignInViewModelFactory(authRepository)
        )[SignInViewModel::class.java]
    }

    private fun initGoogleSignInClient() {
        val id = getString(R.string.google_auth_client_id)
        println("ID = $id")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_auth_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signInUsingGoogle() {
        val signInGoogleIntent = googleSignInClient.signInIntent
        launcher.launch(signInGoogleIntent)
    }


    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    getGoogleAuthCredential(account)
                } catch (e: ApiException) { // Update UI
                    println("ERROR $e")
                }
            }
        }

    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        val googleTokeId = account.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokeId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        //viewModel.signInWithGoogle(googleAuthCredential)
        viewModel.loginStatus.observe(viewLifecycleOwner) { authenticatedUser ->
            when (authenticatedUser) {
                is Response.Failure -> {
                    println("FAILURE")
                }
                is Response.Success -> {
                    println("SUCCESS")
                }
                is Response.Loading -> {
                    println("PROGRESS")
                }
            }
        }
    }

    private fun makeSnackbar(message: String) {
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

    private fun goToHome() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}