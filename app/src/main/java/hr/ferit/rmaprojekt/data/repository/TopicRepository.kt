package hr.ferit.rmaprojekt.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.rmaprojekt.data.model.Enrollment
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import kotlinx.coroutines.tasks.await
import java.util.Date

class TopicRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserTopics(userId: String): List<Topic> {
        return try {
            val snapshot = db.collection("topics").get().await()
            snapshot.toObjects(Topic::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTopicFlashcards(topicId: String): List<Flashcard> {
        return try {
            val snapshot =
                db.collection("topics").document(topicId).collection("flashcards").get().await()
            snapshot.toObjects(Flashcard::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveTopicWithFlashcards(topic: Topic, flashcards: List<Flashcard>, userId: String) {
        topic.creatorId = userId
        topic.createdAt = Date()
        val topicsCollection = db.collection("topics")
        val topicDocument = topicsCollection.add(topic).await()
        val topicId = topicDocument.id

        val flashcardsCollection = topicsCollection.document(topicId).collection("flashcards")
        flashcards.forEach{ flashcard ->
            flashcardsCollection.add(flashcard).await()
        }

        val enrollmentsCollection = topicsCollection.document(topicId).collection("enrollments")
        val enrollment = Enrollment(userId = userId)
        enrollmentsCollection.add(enrollment).await()
    }
}
