package com.raulp.quizgame.data

/**
 * @author Raul Palade
 * @date 13/12/2022
 * @project QuizGame
 */

sealed class Response<T> {
    class Success<T>(val data: T) : Response<T>()
    class Failure<T>(val message: String) : Response<T>()
}