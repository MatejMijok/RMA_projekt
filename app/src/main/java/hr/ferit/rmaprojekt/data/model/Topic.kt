package hr.ferit.rmaprojekt.data.model

import com.google.firebase.firestore.FieldValue

data class Topic(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    var creatorId: String = "",
)
