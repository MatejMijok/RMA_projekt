package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeActivity : ComponentActivity() {

}

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    if (currentUser != null) {
        val uid = currentUser.uid
        val userDocRef = db.collection("users").document(uid)
        LaunchedEffect(userDocRef) {
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userData = document.data
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
    Scaffold (
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar(navController) }
    ) { innerPadding ->
        HomeContent(userData = userData, modifier = Modifier.padding(innerPadding))
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
fun HomeTopBar(navController: NavHostController) {
    TopAppBar(
        title = { Text(text = "Home") },
        navigationIcon = {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()

                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(text = "Logout")
            }
        }
    )
}