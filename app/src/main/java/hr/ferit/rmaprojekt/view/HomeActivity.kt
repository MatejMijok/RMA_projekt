package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.data.model.User
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModel


class HomeActivity : ComponentActivity() {

}

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier, userViewModel: UserViewModel, topicViewmodel: TopicViewModel) {
    val topicsWithFlashcards by topicViewmodel.topicsWithFlashcards.collectAsState()
    val userData = userViewModel.userData.collectAsState().value

    LaunchedEffect(key1 = userData) {
        userViewModel.getUserData()
    }

    Scaffold (
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNewTopic") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        if (userData == null){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ){
                CircularProgressIndicator(
                    modifier = Modifier.size(96.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }else{
            HomeContent(userData = userData, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun HomeContent(userData: User?, modifier: Modifier = Modifier) {
    Text(
        text = "Hello, ${userData?.firstName}!",
        modifier = modifier
    )
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