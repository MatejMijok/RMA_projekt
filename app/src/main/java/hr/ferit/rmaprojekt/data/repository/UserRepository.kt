package hr.ferit.rmaprojekt.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.rmaprojekt.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserData(): User?{
        val currentUser = auth.currentUser
        if(currentUser != null){
            try {
                val document = firestore.collection("users").document(currentUser.uid).get().await()
                if (document.exists()){
                    return document.toObject(User::class.java)
                }
            }catch (e: Exception){
                // TODO()
            }
        }
        return null
    }

    suspend fun registerUser(user: User, password: String): RegistrationResult {
        return try {
            withContext(Dispatchers.IO) {
                val usernameExists = firestore.collection("users")
                    .whereEqualTo("username", user.username)
                    .get().await().documents.isNotEmpty()

                if (usernameExists) {
                    return@withContext RegistrationResult.UsernameTaken
                }

                val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
                val userId = authResult.user?.uid
                if (userId != null) {
                    firestore.collection("users").document(userId).set(user).await()
                }
                RegistrationResult.Success
            }
        } catch (e: Exception) {
            if (e is FirebaseAuthException && e.errorCode == "ERROR_EMAIL_ALREADY_IN_USE"){
                RegistrationResult.EmailInUse
            }else{
                RegistrationResult.Failure(e)
            }
        }
    }

    sealed class RegistrationResult{
        object Success: RegistrationResult()
        object EmailInUse: RegistrationResult()
        object UsernameTaken: RegistrationResult()
        data class Failure(val exception: Exception): RegistrationResult()
    }

    suspend fun loginUser(email: String, password: String): LoginResult {
        return try{
            auth.signInWithEmailAndPassword(email, password).await()
            LoginResult.Success
        }catch(e: Exception){
            LoginResult.Failure(e)
        }
    }

    sealed class LoginResult{
        object Success: LoginResult()
        data class Failure(val exception: Exception): LoginResult()
    }
}