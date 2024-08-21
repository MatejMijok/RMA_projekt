package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModel

class ProfileActivity : ComponentActivity() {
}

@Composable
fun ProfileScreen(navController: NavHostController, modifier: Modifier = Modifier, userViewModel: UserViewModel, topicViewModel: TopicViewModel) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ProfileTopBar(navController, modifier, userViewModel, topicViewModel) },
        bottomBar = { BottomNavBar(navController = navController, userViewModel = userViewModel, topicViewModel = topicViewModel) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(text = userViewModel.userData.value?.firstName ?: "")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavHostController, modifier: Modifier = Modifier, userViewModel: UserViewModel, topicViewModel: TopicViewModel) {
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
                        navController.navigate("welcome") { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }

    )
}