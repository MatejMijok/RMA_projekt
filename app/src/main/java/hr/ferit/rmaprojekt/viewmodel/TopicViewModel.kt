package hr.ferit.rmaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TopicViewModel (private val repository: TopicRepository): ViewModel() {
    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    fun fetchUserTopics(userId: String){
        viewModelScope.launch {
            val result = repository.getUserTopics(userId)
            _topics.value = result
        }
    }

    fun fetchTopicFlashcards(topicId: String){
        viewModelScope.launch {
            val result = repository.getTopicFlashcards(topicId)
            _flashcards.value = result
        }
    }
}