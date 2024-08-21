package hr.ferit.rmaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.data.model.TopicWithFlashcards
import hr.ferit.rmaprojekt.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import hr.ferit.rmaprojekt.data.repository.Result

class TopicViewModel (private val repository: TopicRepository): ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _topicsWithFlashcards = MutableStateFlow<Result<List<TopicWithFlashcards>>>(Result.Loading)
    val topicsWithFlashcards: StateFlow<Result<List<TopicWithFlashcards>>> = _topicsWithFlashcards.asStateFlow()

    var topicWithFlashcards: TopicWithFlashcards? = null

   init{
        getTopics()
    }

    fun getTopics(){
        viewModelScope.launch {
            _topicsWithFlashcards.value = repository.getTopicsWithFlashcards()
        }
    }

    fun clearTopics(){
        viewModelScope.launch {
            _topicsWithFlashcards.value = Result.Loading
        }
    }

    fun saveTopicWithFlashcards(topic: Topic, flashcards: List<Flashcard>){
        firebaseAuth.currentUser?.reload()
        val userId = firebaseAuth.currentUser?.uid

        viewModelScope.launch {
            repository.saveTopicWithFlashcards(topic, flashcards, userId!!)
        }
    }

    fun updateTopicWithFlashcards(topic: Topic, flashcards: List<Flashcard>){
        firebaseAuth.currentUser?.reload()

        viewModelScope.launch{
            repository.updateTopicWithFlashcards(topic, flashcards)
        }
    }

    fun deleteTopic(topic: String){
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }
}