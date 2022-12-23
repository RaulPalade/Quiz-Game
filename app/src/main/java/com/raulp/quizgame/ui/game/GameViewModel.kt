package com.raulp.quizgame.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.data.User
import com.raulp.quizgame.repository.AuthRepository
import com.raulp.quizgame.repository.GameRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GameViewModel(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private var _questionList = MutableLiveData<Response<List<Question>>>()
    val questionList: LiveData<Response<List<Question>>>
        get() = _questionList

    private var _score = MutableLiveData<Response<Boolean>>()
    val score: LiveData<Response<Boolean>>
        get() = _score

    private var _personalRanking = MutableLiveData<Int>()
    val personalRanking: LiveData<Int>
        get() = _personalRanking

    private var _generalRankings = MutableLiveData<Response<List<User>>>()
    val generalRankings: LiveData<Response<List<User>>>
        get() = _generalRankings

    private var _profile = MutableLiveData<Response<User>>()
    val profile: LiveData<Response<User>>
        get() = _profile

    private var _logout = MutableLiveData<Response<Boolean>>()
    val logout: LiveData<Response<Boolean>>
        get() = _logout

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _questionList.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }

    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    fun getUserProfile() {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.getUserProfile()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _profile.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _profile.postValue(Response.Failure("No users were found"))
                    }
                }
            }
        }
    }

    fun getQuestionList(topic: Topic) {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.getQuestionList(topic)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _questionList.postValue(Response.Success(response.data.shuffled()))
                    }
                    is Response.Failure -> {
                        _questionList.postValue(Response.Failure("No questions were found"))
                    }
                }
            }
        }
    }

    fun getRankingList() {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.getRankingList()
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        getPersonalRanking(response.data)
                        _generalRankings.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _generalRankings.postValue(Response.Failure("No users were found"))
                    }
                }
            }
        }
    }

    private fun getPersonalRanking(data: List<User>) {
        data.forEachIndexed { index, user ->
            if (user.id != "") {
                _personalRanking.postValue(index)
            }
        }
    }

    fun updateUserScore(points: Int) {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            val response = gameRepository.updateUserScore(points)
            withContext(Dispatchers.Main) {
                when (response) {
                    is Response.Success -> {
                        _score.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _score.postValue(Response.Failure("Impossible to update score"))
                    }
                }
            }
        }
    }

    fun signOut() {
        job = CoroutineScope(coroutineContext).launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                when (val response = authRepository.signOut()) {
                    is Response.Success -> {
                        _logout.postValue(Response.Success(response.data))
                    }
                    is Response.Failure -> {
                        _logout.postValue(Response.Failure("Impossible to logout"))
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