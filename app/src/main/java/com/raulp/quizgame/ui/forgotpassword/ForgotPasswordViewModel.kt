package com.raulp.quizgame.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulp.quizgame.Response
import com.raulp.quizgame.repository.AuthRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

class ForgotPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var email = MutableLiveData<String>()

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _resetStatus.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }
    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private var _resetStatus = MutableLiveData<Response<Boolean>>()
    val resetStatus: LiveData<Response<Boolean>>
        get() = _resetStatus

    fun restorePassword() {
        val email = email.value.toString()
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = authRepository.resetPassword(email)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _resetStatus.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _resetStatus.postValue(Response.Failure("Email does not exists"))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}