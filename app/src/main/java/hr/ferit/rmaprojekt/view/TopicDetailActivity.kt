package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModel
import hr.ferit.rmaprojekt.data.repository.Result

class TopicDetailActivity : ComponentActivity() {

}

@Composable
fun TopicDetailScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    topicViewModel: TopicViewModel,
    topicId: String
) {
    val topicsWithFlashcards = topicViewModel.topicsWithFlashcards.collectAsState().value
    val topicWithFlashcards = (topicsWithFlashcards as Result.Success).data.find { it.topic.id == topicId }
    var showDialog by remember { mutableStateOf(false) }

    if (topicWithFlashcards != null) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopBar(topicWithFlashcards.topic.name)
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                Text(
                    text = topicWithFlashcards.topic.description,
                    modifier = modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Number of flashcards: ${topicWithFlashcards.flashcards.size}",
                    modifier = modifier
                        .padding(start = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(
                    onClick = {
                        topicViewModel.topicWithFlashcards = topicWithFlashcards
                        navController.navigate("addNewTopic")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        "Edit",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Button(
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Delete",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        navController.navigate("flashcardsScreen/${topicWithFlashcards.topic.id}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        "Start learning",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Delete Topic") },
                    text = { Text("Are you sure you want to delete this topic?") },
                    confirmButton = {
                        Button(onClick = {
                            topicViewModel.deleteTopic(topicWithFlashcards.topic.id)
                            showDialog = false
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                            ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false },
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
        }
    } else {
        Text(text = "Topic not found")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(topicName: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = topicName,
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}
