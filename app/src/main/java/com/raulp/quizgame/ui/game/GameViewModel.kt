package com.raulp.quizgame.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.data.User
import com.raulp.quizgame.repository.GameRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var _questions = MutableLiveData<Response<List<Question>>>()
    val questions: LiveData<Response<List<Question>>>
        get() = _questions

    private var _scoreUpdated = MutableLiveData<Response<Boolean>>()
    val scoreUpdated: LiveData<Response<Boolean>>
        get() = _scoreUpdated

    private var _usersRanking = MutableLiveData<Int>()
    val userRanking: LiveData<Int>
        get() = _usersRanking

    private var _generalRankings = MutableLiveData<Response<List<User>>>()
    val generalRankings: LiveData<Response<List<User>>>
        get() = _generalRankings

    private var _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>>
        get() = _user

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _questions.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }
    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    init {
        getUsersRanking()
    }

    fun getQuestions(topic: Topic) {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.getQuestions(topic)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _questions.postValue(Response.Success(response.data.shuffled()))
                    }
                    is Response.Failure -> {
                        _questions.postValue(Response.Failure("No questions were found"))
                    }
                }
            }
        }
    }

    fun updateUserScore(points: Int) {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.updateUserScore(points)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _scoreUpdated.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _scoreUpdated.postValue(Response.Failure("Impossible to update score"))
                    }
                }
            }
        }
    }

    fun getUsersRanking() {
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

    fun signOut() {}

    fun getUserProfile() {
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

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}