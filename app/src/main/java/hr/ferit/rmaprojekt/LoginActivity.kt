package hr.ferit.rmaprojekt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.ui.theme.RMAProjektTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RMAProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginPage()
                }
            }
        }
    }
}

@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isLoginValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var emailError by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 400.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Log in",
            fontSize = 40.sp,
            modifier = modifier.padding(bottom = 14.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it.text).matches()
                emailError = "Invalid E-mail address"
                            },
            placeholder = {Text(text = "E-mail")},
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 14.dp)
                .widthIn(max = 280.dp),
            singleLine = true,
            isError = !isEmailValid,
            supportingText = {if (!isEmailValid) Text(text = emailError)}
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isLoginValid = true
                            },
            placeholder = {Text(text = "Password")},
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 14.dp)
                .widthIn(max = 280.dp),
            singleLine = true,
            isError = !isLoginValid,
            supportingText = {if (!isLoginValid) Text(text = "E-mail or password is incorrect")}
        )
        Button(
            onClick = {
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email.text, password.text)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            (context as? Activity)?.finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            // You can handle different failure cases here
                        }
                    }
                    .addOnFailureListener{
                        isLoginValid = false
                        isEmailValid = false
                        emailError = "E-mail or password is incorrect"
                    }
            },
            enabled = email.text.isNotEmpty() && password.text.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4B5C92),
                contentColor = Color(0xFFDDE1F9)
            ),
            modifier = modifier
                .width(192.dp)
                .height(48.dp)
        ) {
            Text(
                "Log In",
                fontSize = 18.sp
            )
        }
    }
}