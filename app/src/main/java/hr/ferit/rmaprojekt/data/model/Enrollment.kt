package hr.ferit.rmaprojekt.data.model

import java.util.Date

data class Enrollment(
    val userId: String = "",
    val enrolledAt: Date? = null,
)
