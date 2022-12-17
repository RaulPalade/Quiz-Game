package com.raulp.quizgame.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Topic
import com.raulp.quizgame.repository.GameRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _questions.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }
    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private var _questions = MutableLiveData<Response<List<Question>>>()
    val questions: LiveData<Response<List<Question>>>
        get() = _questions

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
}