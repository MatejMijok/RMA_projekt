package hr.ferit.rmaprojekt.view

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.ferit.rmaprojekt.data.model.User
import hr.ferit.rmaprojekt.data.repository.UserRepository
import hr.ferit.rmaprojekt.viewmodel.UserViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepository()))

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var repeatPassword by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }

    var passwordError by remember { mutableStateOf("") }
    var repeatPasswordError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var emailError by remember { mutableStateOf("") }
    var isUsernameValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isRepeatPasswordValid by remember { mutableStateOf(true) }

    var hasErrors by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Register",
            fontSize = 40.sp,
            modifier = modifier.padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (!isUsernameValid){
                    isUsernameValid = true
                }},
            placeholder = {Text(text = "Username")},
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 5.dp)
                .widthIn(max = 280.dp),
            singleLine = true,
            isError = !isUsernameValid,
            supportingText = {if (!isUsernameValid) {
                Text(text = usernameError)
                }
            }
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = {Text(text = "First name")},
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 21.dp)
                .widthIn(max = 280.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            placeholder = {Text(text = "Last name")},
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 20.dp)
                .widthIn(max = 280.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it.text).matches()
                            },
            placeholder = {Text(text = "E-mail")},
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .widthIn(max = 280.dp)
                .padding(bottom = 4.dp),
            singleLine = true,
            isError = !isEmailValid,
            supportingText = {if (!isEmailValid) {
                Text(text = "Invalid e-mail address")
                }
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (password.text != repeatPassword.text) {
                    isPasswordValid = false
                    passwordError = "Passwords do not match"
                    repeatPasswordError = "Passwords do not match"
                } else {
                    isPasswordValid = true
                    passwordError = ""
                    repeatPasswordError = ""
                }
            },
            placeholder = { Text(text = "Password") },
            shape = RoundedCornerShape(15.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = modifier
                .widthIn(max = 280.dp),
            singleLine = true,
            isError = !isPasswordValid,
            supportingText = {if (!isPasswordValid) {
                Text(text = passwordError)
            }else{
                Text(text = "")
            }
            }
        )
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = {
                repeatPassword = it
                if (password.text != repeatPassword.text) {
                    isPasswordValid = false
                    isRepeatPasswordValid = false
                    passwordError = "Passwords do not match"
                    repeatPasswordError = "Passwords do not match"
                } else {
                    isPasswordValid = true
                    isRepeatPasswordValid = true
                    passwordError = ""
                    repeatPasswordError = ""
                }},
            placeholder = {Text(text = "Repeat password")},
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .widthIn(max = 280.dp)
                .padding(bottom = 2.dp),
            singleLine = true,
            isError = !isRepeatPasswordValid,
            supportingText = {if (!isRepeatPasswordValid) {
                Text(text = repeatPasswordError)
            }else{
                Text(text = "")
                }
            }
        )
        Button(
            onClick = {
                if(!hasErrors){
                    val user = User(
                        username = username.text,
                        firstName = firstName.text,
                        lastName = lastName.text,
                        email = email.text
                    )
                    viewModel.viewModelScope.launch{
                        viewModel.registerUser(user, password.text)
                    }
                }
            },
            enabled = username.text.isNotEmpty() && email.text.isNotEmpty() && password.text.isNotEmpty() && repeatPassword.text.isNotEmpty() && !hasErrors,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4B5C92),
                contentColor = Color(0xFFDDE1F9)
            ),
            modifier = modifier
                .width(192.dp)
                .height(48.dp)
        ) {
            Text(
                "Register",
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.ime)
        )

        val registrationStatus by viewModel.registrationStatus.collectAsState()
        LaunchedEffect(key1 = registrationStatus) {
            when(registrationStatus){
                is UserRepository.RegistrationResult.Success -> {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
                is UserRepository.RegistrationResult.EmailInUse -> {
                    emailError = "Email already in use"
                    isEmailValid = false
                }
                is UserRepository.RegistrationResult.UsernameTaken -> {
                    usernameError = "Username already taken"
                    isUsernameValid = false
                }
                null -> {}
                is UserRepository.RegistrationResult.Failure -> {}
            }
        }
        }
}