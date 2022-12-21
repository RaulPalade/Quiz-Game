package com.raulp.quizgame.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.User
import com.raulp.quizgame.repository.GameRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _generalRankings.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }

    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private var _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>>
        get() = _user

    private var _generalRankings = MutableLiveData<Response<List<User>>>()
    val generalRankings: LiveData<Response<List<User>>>
        get() = _generalRankings

    private var _usersRanking = MutableLiveData<Int>()
    val userRanking: LiveData<Int>
        get() = _usersRanking

    fun signOut() {}

    private fun getUserProfile() {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.getUserProfile()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _user.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _user.postValue(Response.Failure("No users were found"))
                    }
                }
            }
        }
    }

    private fun getUsersRanking() {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.getUsersRanking()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        getCurrentUserRanking(response.data)
                        _generalRankings.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _generalRankings.postValue(Response.Failure("No users were found"))
                    }
                }
            }
        }
    }

    private fun getCurrentUserRanking(data: List<User>) {
        data.forEachIndexed { index, user ->
            if (user.id != "") {
                _usersRanking.postValue(index)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}