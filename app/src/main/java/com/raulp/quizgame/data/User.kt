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
    var id: String,
    val name: String,
    val email: String,
    val score: Int = 0,
    val profileImage: String = "",
    @ServerTimestamp
    val memberSince: Date? = null
) : Serializable {
    constructor(name: String, email: String) : this(
        id = "",
        name = name,
        email = email,
        score = 0,
        profileImage = ""
    )

    constructor(name: String, score: Int) : this(
        id = "",
        name = name,
        email = "",
        score = score,
        profileImage = ""
    )

    constructor(name: String, email: String, score: Int, profileImage: String) : this(
        id = "",
        name = name,
        email = email,
        score = score,
        profileImage = profileImage
    )

    constructor(name: String, email: String, profileImage: String) : this(
        id = "",
        name = name,
        email = email,
        score = 0,
        profileImage = profileImage
    )
}