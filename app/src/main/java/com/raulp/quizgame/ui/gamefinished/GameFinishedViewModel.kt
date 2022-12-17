package com.raulp.quizgame.ui.gamefinished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.User
import com.raulp.quizgame.repository.GameRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GameFinishedViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _rankings.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }
    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private var _rankings = MutableLiveData<Response<List<User>>>()
    val rankings: LiveData<Response<List<User>>>
        get() = _rankings

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}