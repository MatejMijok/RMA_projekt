package hr.ferit.rmaprojekt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.rmaprojekt.ui.theme.RMAProjektTheme


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RMAProjektTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { HomeTopBar(activity = this@HomeActivity) }
                ) { innerPadding ->
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val db = FirebaseFirestore.getInstance()

                    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }

                    if (currentUser != null) {
                        val uid = currentUser.uid
                        val userDocRef = db.collection("users").document(uid)
                        userDocRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    userData = document.data
                                }
                            }
                            .addOnFailureListener { _ ->
                                // Handle failure
                            }
                    }

                    HomeContent(userData = userData, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HomeContent(userData: Map<String, Any>?, modifier: Modifier = Modifier) {
    userData?.let { data ->
        val username = data["username"] as? String ?: "User"
        Text(
            text = "Hello, $username!",
            modifier = modifier
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(activity: Activity) {
    TopAppBar(
        title = { Text(text = "Home") },
        navigationIcon = {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(text = "Logout")
            }
        }
    )
}