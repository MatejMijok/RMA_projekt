package hr.ferit.rmaprojekt


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.ui.theme.RMAProjektTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContent {
            RMAProjektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WelcomePage()
                }
            }
        }
    }
}

@Composable
fun WelcomePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Text(
            text = "Welcome",
            fontSize = 40.sp,
            modifier = modifier.padding(bottom = 14.dp)
        )
        Button(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4B5C92),
                contentColor = Color(0xFFDDE1F9)
            ),
            modifier = modifier
                .width(192.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Log In",
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4B5C92),
                contentColor = Color(0xFFDDE1F9)
            ),
            modifier = modifier
                .width(192.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                )
        }
    }
}