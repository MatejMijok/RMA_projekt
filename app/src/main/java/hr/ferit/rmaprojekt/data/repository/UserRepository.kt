package hr.ferit.rmaprojekt.data.repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
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
        auth.currentUser?.reload()?.await()
        val currentUser = auth.currentUser
        if(currentUser != null){
            try {
                val document = firestore.collection("users").document(currentUser.uid).get().await()
                if (document.exists()){
                    return document.toObject(User::class.java)
                }
            }catch (e: Exception){
                Log.e("UserRepository.kt", "Error getting user data: $e")
            }
        }
        return null
    }

    suspend fun registerUser(user: User, password: String): RegistrationResult {
        return try {
            withContext(Dispatchers.IO) {
                val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
                auth.currentUser?.reload()?.await()
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

    suspend fun changePassword(newPassword: String, currentPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        auth.currentUser?.reload()?.await()
        val currentUser = auth.currentUser

        val credential = EmailAuthProvider.getCredential(currentUser?.email ?: "", currentPassword)

        currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                currentUser.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if(updateTask.isSuccessful){
                        onSuccess()
                    }else{
                        val exception = updateTask.exception ?: Exception("Password change failed")
                        onFailure(exception)
                    }
                }
            }else{
                val exception = task.exception ?: Exception("Password change failed")
                onFailure(exception)
            }
        }
    }

    suspend fun saveUserData(firstName: String, lastName: String, email: String, currentPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        auth.currentUser?.reload()?.await()
        val currentUser = auth.currentUser
        val credential = EmailAuthProvider.getCredential(currentUser?.email ?: "", currentPassword)
        currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                currentUser.verifyBeforeUpdateEmail(email).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful){
                        updateUserData(firstName, lastName, email, onSuccess, onFailure)
                    }else{
                        val exception = updateTask.exception ?: Exception("Email verification failed")
                        onFailure(exception)
                    }
                }
            }else {
                val exception = task.exception ?: Exception("Saving failed")
                onFailure(exception)
            }
        }

    }

    fun updateUserData(firstName: String, lastName: String, email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        auth.currentUser?.reload()
        val currentUser = auth.currentUser

        val userDocument = firestore.collection("users").document(currentUser?.uid!!)

        userDocument.update(mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email
        ))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener{ exception ->
                onFailure(exception)
            }
    }

    fun getUserId() : String{
        auth.currentUser?.reload()
        val currentUser = auth.currentUser
        Log.d("UserRepository", "User ID: ${currentUser?.uid}")
        return currentUser?.uid ?: ""
    }

    sealed class RegistrationResult{
        object Success: RegistrationResult()
        object EmailInUse: RegistrationResult()
        data class Failure(val exception: Exception): RegistrationResult()
    }

    suspend fun loginUser(email: String, password: String): LoginResult {
        return try{
            auth.signInWithEmailAndPassword(email, password).await()
            auth.currentUser?.reload()?.await()
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