package hr.ferit.rmaprojekt.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import hr.ferit.rmaprojekt.data.model.Enrollment
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.data.model.TopicWithFlashcards
import kotlinx.coroutines.tasks.await

class TopicRepository {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun getTopicsWithFlashcards(): Result<List<TopicWithFlashcards>>{
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            val currentUserId = firebaseAuth.currentUser?.uid ?: return Result.Failure(Exception("User not authenticated"))

            val topics = db.collection("topics")
                .whereEqualTo("creatorId", currentUserId)
                .get().await().toObjects(Topic::class.java)

            val topicsWithFlashcards = topics.map { topic ->
                val flashcards = db.collection("topics/${topic.id}/flashcards")
                    .get().await().toObjects(Flashcard::class.java)
                TopicWithFlashcards(topic, flashcards)
            }
            Log.d("TopicRepository", "Topics with flashcards: $topics")

            Result.Success(topicsWithFlashcards)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun saveTopicWithFlashcards(topic: Topic, flashcards: List<Flashcard>, userId: String) {
        topic.creatorId = userId
        val topicsCollection = db.collection("topics")
        val topicDocument = topicsCollection.add(topic).await()
        val topicId = topicDocument.id

        topicDocument.update("id", topicId).await()

        val flashcardsCollection = topicsCollection.document(topicId).collection("flashcards")
        flashcards.forEach{ flashcard ->
            flashcardsCollection.add(flashcard).await()
        }

        val enrollmentsCollection = topicsCollection.document(topicId).collection("enrollments")
        val enrollment = Enrollment(userId = userId)
        enrollmentsCollection.add(enrollment).await()
    }

   suspend fun updateTopicWithFlashcards(topic: Topic, flashcards: List<Flashcard>){
       firebaseAuth.currentUser?.reload()?.await()
       try{
           val topicDocument = db.collection("topics").document(topic.id)
           topicDocument.set(topic, SetOptions.merge()).await()

           val flashcardsCollection = topicDocument.collection("flashcards")
           flashcardsCollection.get().await().documents.forEach{ document ->
               document.reference.delete().await()
           }
           flashcards.forEach{ flashcard ->
               flashcardsCollection.add(flashcard).await()
           }
        }catch(e: Exception){
            //
        }
    }

    suspend fun deleteTopic(topicId: String){
        try {
            val topicDocument = db.collection("topics")
            topicDocument.document(topicId).delete().await()
        }catch (e: Exception){
            //
        }
    }
}

sealed class Result<out T>{
    data class Success<out T>(val data: T): Result<T>()
    data class Failure(val exception: Exception): Result<Nothing>()
    object Loading: Result<Nothing>()
}
