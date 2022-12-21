package com.raulp.quizgame.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SettingsViewModel : ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            //_generalRankings.postValue(Response.Failure("Exception handled: ${throwable.localizedMessage}"))
        }
    }

    private val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}