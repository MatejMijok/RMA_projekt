package hr.ferit.rmaprojekt.view

import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModel

class ProfileActivity : ComponentActivity() {
}

@Composable
fun ProfileScreen(navController: NavHostController, modifier: Modifier = Modifier, userViewModel: UserViewModel, topicViewModel: TopicViewModel) {
    var firstName by remember { mutableStateOf(userViewModel.userData.value?.firstName ?: "") }
    var lastName by remember { mutableStateOf(userViewModel.userData.value?.lastName ?: "") }
    var email by remember { mutableStateOf(userViewModel.userData.value?.email ?: "") }

    var isEmailValid by remember { mutableStateOf(true) }
    var emailError by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    val userId = userViewModel.getUserId()

    val clipboardManager = LocalContext.current.getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ProfileTopBar(navController, userViewModel, topicViewModel) },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(userViewModel.isAnonymous){
                Text(text = "Anonymous user", style = MaterialTheme.typography.headlineSmall, modifier = modifier.padding(8.dp))
                Text(text = "Your data will lost if you reinstall without registering", style = MaterialTheme.typography.bodyMedium, modifier = modifier.padding(8.dp))
                Text(text = "You can register by clicking the button below", style = MaterialTheme.typography.bodySmall, modifier = modifier.padding(8.dp))
                Button(
                    modifier = modifier
                        .width(192.dp)
                        .height(48.dp)
                        .padding(bottom = 10.dp),
                    onClick = {
                        navController.navigate("register")
                    }
                ) {
                    Text(text = "Register")
                }
                Spacer(modifier = modifier.weight(1f))
                Button(
                    modifier = modifier
                        .width(192.dp)
                        .height(48.dp)
                        .padding(bottom = 10.dp),
                    onClick = {
                        val clipData = android.content.ClipData.newPlainText("user_id", userId)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(navController.context, "User ID copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Copy user ID")
                }
                Text(text = "User ID: $userId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.surfaceVariant)
            }else{
                Text(text = "Personal Information", style = MaterialTheme.typography.headlineSmall, modifier = modifier.padding(8.dp))
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                    },
                    placeholder = {Text(text = "First name")},
                    shape = RoundedCornerShape(15.dp),
                    modifier = modifier
                        .padding(bottom = 10.dp)
                        .widthIn(max = 280.dp),
                    singleLine = true,
                    supportingText = { Text(text = "") }
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                    },
                    placeholder = {Text(text = "Last name")},
                    shape = RoundedCornerShape(15.dp),
                    modifier = modifier
                        .padding(bottom = 10.dp)
                        .widthIn(max = 280.dp),
                    singleLine = true,
                    supportingText = { Text(text = "") }
                )
                Text(text = "Account Settings", style = MaterialTheme.typography.headlineSmall, modifier = modifier.padding(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                        emailError = "Invalid E-mail address"
                    },
                    placeholder = {Text(text = "E-mail")},
                    shape = RoundedCornerShape(15.dp),
                    modifier = modifier
                        .padding(bottom = 10.dp)
                        .widthIn(max = 280.dp),
                    singleLine = true,
                    isError = !isEmailValid,
                    supportingText = { if (!isEmailValid) Text(text = emailError) }
                )
                Button(
                    enabled = email.isNotEmpty() && isEmailValid,
                    modifier = modifier
                        .width(192.dp)
                        .height(48.dp)
                        .padding(bottom = 10.dp),
                    onClick = {
                        showUpdateDialog = true
                    }
                ) {
                    Text(text = "Save")
                }
                Button(
                    modifier = modifier
                        .width(192.dp)
                        .height(48.dp)
                        .padding(bottom = 10.dp),
                    onClick = {
                        showPasswordDialog = true
                    }
                ) {
                    Text(text = "Change password")
                }
                Spacer(modifier = modifier.weight(1f))
                Button(
                    modifier = modifier
                        .width(192.dp)
                        .height(48.dp)
                        .padding(bottom = 10.dp),
                    onClick = {
                        val clipData = android.content.ClipData.newPlainText("user_id", userId)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(navController.context, "User ID copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = "Copy user ID")
                }
                Text(text = "User ID: $userId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.surfaceVariant)
                if (showPasswordDialog) {

                    var currentPassword by remember { mutableStateOf("")}
                    var password by remember { mutableStateOf("") }
                    var repeatPassword by remember { mutableStateOf("") }

                    var isPasswordValid by remember { mutableStateOf(true) }
                    var passwordError by remember { mutableStateOf("") }

                    var isRepeatPasswordValid by remember { mutableStateOf(true) }
                    var repeatPasswordError by remember { mutableStateOf("") }

                    var currentPasswordError by remember { mutableStateOf("") }
                    var isCurrentPasswordValid by remember { mutableStateOf(true) }

                    AlertDialog(
                        onDismissRequest = { showPasswordDialog = false },
                        title = { Text("Change password", style = MaterialTheme.typography.headlineSmall) },
                        text = {
                            Column(
                                modifier = modifier.padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = currentPassword,
                                    onValueChange = {
                                        currentPassword = it
                                        isCurrentPasswordValid = true
                                        currentPasswordError = ""
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    modifier = modifier.padding(bottom = 4.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    supportingText = { Text(text = currentPasswordError) },
                                    isError = !isCurrentPasswordValid,
                                    placeholder = { Text(text = "Current password") }
                                )
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        if(password != repeatPassword){
                                            isPasswordValid = false
                                            passwordError = "Passwords do not match"
                                            isRepeatPasswordValid = false
                                            repeatPasswordError = "Passwords do not match"
                                        }else{
                                            isPasswordValid = true
                                            passwordError = ""
                                            isRepeatPasswordValid = true
                                            repeatPasswordError = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    modifier = modifier.padding(bottom = 8.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    isError = !isPasswordValid,
                                    supportingText = { if (!isPasswordValid) Text(text = passwordError) },
                                    placeholder = { Text(text = "New password") }
                                )
                                OutlinedTextField(
                                    value = repeatPassword,
                                    onValueChange = {
                                        repeatPassword = it
                                        if(password != repeatPassword){
                                            isRepeatPasswordValid = false
                                            repeatPasswordError = "Passwords do not match"
                                        }else{
                                            isRepeatPasswordValid = true
                                            repeatPasswordError = ""
                                            isPasswordValid = true
                                            passwordError = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    isError = !isRepeatPasswordValid,
                                    supportingText = { if (!isRepeatPasswordValid) Text(text = repeatPasswordError) },
                                    placeholder = { Text(text = "Repeat new password") }
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                userViewModel.changePassword(
                                    password,
                                    currentPassword,
                                    onSuccess = {
                                        showPasswordDialog = false
                                    },
                                    onFailure = { exception ->
                                        Log.d("ProfileScreen", "Error changing password: $exception")
                                        currentPasswordError = "Current password is incorrect"
                                        isCurrentPasswordValid = false
                                    }
                                )
                            },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                enabled = password == repeatPassword && isPasswordValid && isRepeatPasswordValid && currentPassword.isNotEmpty() && isCurrentPasswordValid
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showPasswordDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer

                                )
                            ){
                                Text("No")
                            }
                        }
                    )
                }
                if (showUpdateDialog) {
                    var currentPassword by remember { mutableStateOf("")}

                    var currentPasswordError by remember { mutableStateOf("") }
                    var isCurrentPasswordValid by remember { mutableStateOf(true) }

                    AlertDialog(
                        onDismissRequest = { showUpdateDialog = false },
                        title = { Text("Save changes", style = MaterialTheme.typography.headlineSmall) },
                        text = {
                            Column(
                                modifier = modifier.padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = currentPassword,
                                    onValueChange = {
                                        currentPassword = it
                                        isCurrentPasswordValid = true
                                        currentPasswordError = ""
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    supportingText = { Text(text = currentPasswordError) },
                                    isError = !isCurrentPasswordValid,
                                    placeholder = { Text(text = "Current password") }
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                userViewModel.saveUserData(
                                    firstName,
                                    lastName,
                                    email,
                                    currentPassword,
                                    onSuccess = {
                                        showUpdateDialog = false
                                        userViewModel.clearUserData()
                                        userViewModel.getUserData()
                                        navController.navigate("home") { popUpTo(0) { inclusive = true } }
                                    },
                                    onFailure = { exception ->
                                        Log.d("ProfileScreen", "Error saving user data: $exception")
                                        currentPasswordError = "Password is incorrect"
                                        isCurrentPasswordValid = false
                                    }
                                )
                            },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                enabled = currentPassword.isNotEmpty() && isCurrentPasswordValid
                            ) {
                                Text("Continue")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showUpdateDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer

                                )
                            ){
                                Text("Close")
                            }
                        }
                    )
                }
            }
            }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavHostController, userViewModel: UserViewModel, topicViewModel: TopicViewModel) {
    var isExpanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = { Text(text = "Profile", style = MaterialTheme.typography.headlineLarge) },
        actions = {
            IconButton(onClick = { isExpanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                DropdownMenuItem(
                    text = { Text(text = "Log out") },
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        userViewModel.clearUserData()
                        topicViewModel.clearTopics()
                        userViewModel.resetLoginStatus()
                        userViewModel.resetRegisterStatus()
                        userViewModel.isAnonymous = false
                        navController.navigate("welcome") { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    )
}