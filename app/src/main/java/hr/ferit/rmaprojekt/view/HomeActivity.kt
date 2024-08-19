package hr.ferit.rmaprojekt.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.data.model.User
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModel
import hr.ferit.rmaprojekt.data.repository.Result

class HomeActivity : ComponentActivity() {
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val userViewModel: UserViewModel by viewModels()
    private val topicViewModel: TopicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null){
                userViewModel.getUserData()
                topicViewModel.getTopics()
            }else{
                userViewModel.clearUserData()
                topicViewModel.clearTopics()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }
}

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier, userViewModel: UserViewModel, topicViewmodel: TopicViewModel) {
    val topicsWithFlashcards by topicViewmodel.topicsWithFlashcards.collectAsState()
    val userData = userViewModel.userData.collectAsState().value

    LaunchedEffect(key1 = userData) {
        userViewModel.getUserData()
    }

    LaunchedEffect(key1 = topicsWithFlashcards) {
        topicViewmodel.getTopics()
    }

    Scaffold (
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar(navController, userViewModel, topicViewmodel) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNewTopic") }, containerColor = MaterialTheme.colorScheme.primary) {
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
            when(topicsWithFlashcards){
                is Result.Loading -> {
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
                }
                is Result.Success -> {
                    val topics = (topicsWithFlashcards as Result.Success).data.map { it.topic }
                    HomeContent(userData = userData, modifier = Modifier.padding(innerPadding), topics = topics, navController)
                }
                is Result.Failure -> {
                    Text(text = "Failed to load topics", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HomeContent(userData: User?, modifier: Modifier = Modifier, topics: List<Topic>, navController: NavHostController) {
    Column(
        modifier = modifier.padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ){
        TopicList(topics = topics){ topic ->
            navController.navigate("topicDetails/${topic.id}")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavHostController, userViewModel: UserViewModel, topicViewModel: TopicViewModel) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Home", style = MaterialTheme.typography.headlineLarge) },
        navigationIcon = {
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    userViewModel.clearUserData()
                    topicViewModel.clearTopics()
                    userViewModel.resetLoginStatus()

                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(text = "Logout")
            }
        }
    )
}

@Composable
fun TopicCard(topic: Topic, modifier: Modifier = Modifier, onTopicClick: (Topic) -> Unit){
    Card(
        modifier = modifier.padding(bottom = 8.dp)
            .clickable { onTopicClick(topic) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Text(
                text = topic.name,
                style = MaterialTheme.typography.headlineSmall
                )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = topic.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TopicList(topics: List<Topic>, modifier: Modifier = Modifier, onTopicClick: (Topic) -> Unit){
    LazyColumn(
        modifier = modifier.padding(8.dp)
    ){
        items(topics.size){ index ->
            TopicCard(topic = topics[index], onTopicClick = onTopicClick)
        }
    }
}