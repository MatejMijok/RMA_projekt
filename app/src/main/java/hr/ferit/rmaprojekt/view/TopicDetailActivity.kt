package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                        .padding(16.dp)
                ) {
                    Text(
                        "Start learning",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
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
