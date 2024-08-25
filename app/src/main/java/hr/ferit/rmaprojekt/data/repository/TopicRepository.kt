package hr.ferit.rmaprojekt.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import hr.ferit.rmaprojekt.data.model.Enrollment
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.data.model.TopicWithFlashcards
import kotlinx.coroutines.tasks.await
import java.io.InputStream

class TopicRepository {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun getTopicsWithFlashcards(): Result<List<TopicWithFlashcards>>{
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            val currentUserId = firebaseAuth.currentUser?.uid ?: return Result.Failure(Exception("User not authenticated"))

            val enrollmentDocs = db.collection("enrollments")
                .whereEqualTo("userId", currentUserId)
                .get().await()

            Log.d("TopicRepository", "Enrollment documents: $enrollmentDocs")

            val topicIds = enrollmentDocs.documents.mapNotNull { it.getString("topicId") }
            Log.d("TopicRepository", "Topic IDs: $topicIds")

            val topics = topicIds.mapNotNull { topicId ->
                db.collection("topics").document(topicId).get().await().toObject(Topic::class.java)
            }

            Log.d("TopicRepository", "Topics: $topics")

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
        val enrollmentsCollection = db.collection("enrollments")
        val topicDocument = topicsCollection.add(topic).await()
        val topicId = topicDocument.id

        topicDocument.update("id", topicId).await()

        val flashcardsCollection = topicsCollection.document(topicId).collection("flashcards")
        flashcards.forEach{ flashcard ->
            flashcardsCollection.add(flashcard).await()
        }

        val enrollment = Enrollment(userId = userId, topicId = topicId)
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

    suspend fun addEnrollment(topicId: String, userId: String){
        firebaseAuth.currentUser?.reload()?.await()
        try{
            val enrollmentCollection = db.collection("enrollments")
            val enrollment = Enrollment(userId = userId, topicId = topicId)
            enrollmentCollection.add(enrollment).await()
        }catch (e: Exception){
            //
        }
    }

    suspend fun uploadImage(imageStream: InputStream): String?{
        return try{
            val storageRef = storage.reference
            val imageRef = storageRef.child("flashcard_images/${System.currentTimeMillis()}.jpg")
            imageRef.putStream(imageStream).await()
            imageRef.downloadUrl.await().toString()
        }catch (e: Exception){
            null
        }
    }

    fun deleteImage(imageUrl: String){
        try{
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.delete()
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
