package hr.ferit.rmaprojekt.data.model

import java.util.Date

data class Topic(
    val name: String = "",
    val description: String = "",
    var creatorId: String = "",
    var createdAt: Date? = null,
)
