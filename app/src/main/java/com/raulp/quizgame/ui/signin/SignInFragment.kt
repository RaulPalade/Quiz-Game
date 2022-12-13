package com.raulp.quizgame.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.raulp.quizgame.ResponseState
import com.raulp.quizgame.databinding.FragmentSignInBinding
import com.raulp.quizgame.repository.AuthRepository

private const val REQUEST_CODE_GOOGLE_SIGN_IN = 1 /* unique request id */
private const val TAG = "LOGIN" /* unique request id */

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

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it == true) {
                goToHome()
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

    /*private fun signInUsingGoogle() {
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
        }*/

    private fun signInUsingGoogle() {
        val signInGoogleIntent = googleSignInClient.signInIntent
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                getGoogleAuthCredential(account)
            } catch (e: ApiException) {
                /* Update UI */
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
        viewModel.signInWithGoogle(googleAuthCredential)
        viewModel.authenticateUserLiveData.observe(viewLifecycleOwner) { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    authenticatedUser.message?.let {
                        println("AUTHENTICATED")
                    }
                }
                is ResponseState.Success -> {
                    if (authenticatedUser.data != null) {
                        println("SUCCESS")
                    }
                }
                is ResponseState.Loading -> {
                    println("PROGRESS")
                }
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}