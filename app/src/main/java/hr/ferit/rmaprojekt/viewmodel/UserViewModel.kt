package hr.ferit.rmaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.rmaprojekt.data.model.User
import hr.ferit.rmaprojekt.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository): ViewModel() {
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()
    private val _registrationStatus = MutableStateFlow<UserRepository.RegistrationResult?>(null)
    val registrationStatus: StateFlow<UserRepository.RegistrationResult?> = _registrationStatus.asStateFlow()
    private val _loginStatus = MutableStateFlow<UserRepository.LoginResult?>(null)
    val loginStatus: StateFlow<UserRepository.LoginResult?> = _loginStatus.asStateFlow()

    init {
        getUserData()
    }

    fun getUserData(){
        viewModelScope.launch{
            _userData.value = repository.getUserData()
        }
    }

    fun clearUserData(){
        viewModelScope.launch {
            _userData.value = null
        }
    }

    fun resetLoginStatus() {
        _loginStatus.value = null
    }

    suspend fun registerUser(user: User, password: String){
        viewModelScope.launch {
            _registrationStatus.value = repository.registerUser(user, password)
        }
    }

    suspend fun loginUser(email: String, password: String){
        viewModelScope.launch {
            _loginStatus.value = repository.loginUser(email, password)
        }
    }
}
