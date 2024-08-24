package hr.ferit.rmaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    var currentUserId: String? = null
    var isAnonymous = FirebaseAuth.getInstance().currentUser?.providerData?.any { it.providerId == "firebase" } ?: false

    init {
        getUserData()
    }

    fun getUserData(){
        viewModelScope.launch{
            _userData.value = repository.getUserData()
            currentUserId = repository.getUserId()
        }
    }

    fun getUserId() : String {
        return repository.getUserId()
    }

    fun clearUserData(){
        viewModelScope.launch {
            _userData.value = null
        }
    }

    fun resetLoginStatus() {
        _loginStatus.value = null
    }

    fun resetRegisterStatus(){
        _registrationStatus.value = null
    }

    suspend fun registerUser(user: User, password: String){
        viewModelScope.launch {
            _registrationStatus.value = repository.registerUser(user, password, isAnonymous)
            isAnonymous = false
        }
    }

    suspend fun loginUser(email: String, password: String){
        viewModelScope.launch {
            _loginStatus.value = repository.loginUser(email, password)
            isAnonymous = false
        }
    }

    fun changePassword(newPassword: String, currentPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            repository.changePassword(newPassword, currentPassword, onSuccess, onFailure)
        }
    }

    fun saveUserData(firstName: String, lastName: String, email: String, currentPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            repository.saveUserData(firstName, lastName, email, currentPassword, onSuccess, onFailure)
        }
    }

    fun continueWithoutRegistering(onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            repository.continueWithoutRegistering(onSuccess, onFailure)
            isAnonymous = true
        }
    }
}
