package hr.ferit.rmaprojekt.data.model

import java.util.Date

data class Topic(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val creatorId: String = "",
    val createdAt: Date? = null,
    val flashcards: List<Flashcard> = emptyList(),
    val enrollments: List<Enrollment> = emptyList()
)
