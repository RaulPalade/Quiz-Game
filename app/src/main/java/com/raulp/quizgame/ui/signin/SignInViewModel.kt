package com.raulp.quizgame.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.User
import com.raulp.quizgame.repository.AuthRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private lateinit var auth: FirebaseAuth

    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _loginStatus.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }
    val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    /* private var _loginStatus = MutableLiveData<Response<Boolean>>()
     val loginStatus: LiveData<Response<Boolean>>
         get() = _loginStatus*/

    private var _loginStatus = MutableLiveData<Response<User>>()
    val loginStatus: LiveData<Response<User>>
        get() = _loginStatus

    fun checkIfUserLoggedIn(): Boolean {
        val user = Firebase.auth.currentUser
        return user != null
    }

    fun logOut() {
        Firebase.auth.signOut();
    }

    suspend fun signIn() {
        val email = email.value.toString()
        val password = password.value.toString()

        /*auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            _loginStatus.postValue(Response.Success(true))
        }.addOnFailureListener {
            _loginStatus.postValue(Response.Failure("Error during login"))
        }*/

        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = authRepository.signIn(email, password)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        val user = response.data
                        _loginStatus.postValue(Response.Success(user))
                    }
                    is Response.Failure -> {
                        _loginStatus.postValue(Response.Failure("Error during login"))
                    }
                    else -> {}
                }
            }
        }
    }

    /*fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        auth = Firebase.auth
        auth.signInWithCredential(googleAuthCredential).addOnSuccessListener {
            _loginStatus.postValue(Response.Success(true))
        }.addOnFailureListener {
            _loginStatus.postValue(Response.Failure("Error during login"))
        }
    }*/
}