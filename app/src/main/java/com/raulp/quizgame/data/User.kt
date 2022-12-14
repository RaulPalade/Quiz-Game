package com.raulp.quizgame.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

/**
 * @author Raul Palade
 * @date 12/12/2022
 * @project QuizGame
 */

data class User(
    @DocumentId
    val id: String,
    val name: String,
    val email: String,
    @ServerTimestamp
    val timestamp: Date? = null
) : Serializable {
    constructor(name: String, email: String) : this(
        id = "",
        name = name,
        email = email
    )
}