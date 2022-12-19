package com.raulp.quizgame.ui.signup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var name = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    private var profileImage = MutableLiveData<Uri>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _registerStatus.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }
    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private var _registerStatus = MutableLiveData<Response<Boolean>>()
    val registerStatus: LiveData<Response<Boolean>>
        get() = _registerStatus

    fun signUp() {
        val name = name.value.toString()
        val email = email.value.toString()
        val password = password.value.toString()

        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = authRepository.signUp(email, password)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        val user =
                            User(
                                id = response.data,
                                name = name,
                                email = email,
                                profileImage = profileImage.value.toString()
                            )
                        addUserOnFirestore(user)
                    }
                    is Response.Failure -> {
                        _registerStatus.postValue(Response.Failure("Error during registration"))
                    }
                }
            }
        }
    }

    private fun addUserOnFirestore(user: User) {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = profileImage.value?.let { authRepository.addUserOnFirestore(user, it) }
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        sendVerificationEmail()
                    }
                    is Response.Failure -> {
                        _registerStatus.postValue(Response.Failure("Error during firestore save"))
                    }
                    else -> {
                        _registerStatus.postValue(Response.Failure("Error during uploading photo"))
                    }
                }
            }
        }
    }

    private fun sendVerificationEmail() {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = authRepository.sendVerificationEmail()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _registerStatus.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _registerStatus.postValue(Response.Failure("Error during email sendind"))
                    }
                }
            }
        }
    }

    fun setProfileImage(image: Uri) {
        profileImage.value = image
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}