package hr.ferit.rmaprojekt.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import kotlinx.coroutines.tasks.await

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
}
