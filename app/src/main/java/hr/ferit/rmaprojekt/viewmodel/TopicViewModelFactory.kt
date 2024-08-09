package hr.ferit.rmaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hr.ferit.rmaprojekt.data.repository.TopicRepository

class TopicViewModelFactory(private val repository: TopicRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TopicViewModel::class.java)){
            return TopicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}