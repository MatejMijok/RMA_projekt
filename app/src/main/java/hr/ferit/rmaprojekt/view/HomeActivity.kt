package hr.ferit.rmaprojekt.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.data.model.Topic
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }
}

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier, userViewModel: UserViewModel, topicViewmodel: TopicViewModel) {
    val topicsWithFlashcards by topicViewmodel.topicsWithFlashcards.collectAsState()
    val userData by userViewModel.userData.collectAsState()

    LaunchedEffect(key1 = Unit) {
        userViewModel.getUserData()
        topicViewmodel.getTopics()
    }

    Scaffold (
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar() },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                topicViewmodel.topicWithFlashcards = null
                navController.navigate("addNewTopic") },
                containerColor = MaterialTheme.colorScheme.primary) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        if (userData == null && !userViewModel.isAnonymous){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
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
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
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
                    HomeContent(modifier = Modifier.padding(innerPadding), topics = topics, navController)
                }
                is Result.Failure -> {
                    Text(text = "Failed to load topics", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HomeContent(modifier: Modifier = Modifier, topics: List<Topic>, navController: NavHostController) {
    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ){
        if(topics.isNotEmpty()){
            TopicList(topics = topics){ topic ->
                navController.navigate("topicDetails/${topic.id}")
            }
        }else{
            Text(text = "No topics found", modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally), style = MaterialTheme.typography.headlineMedium)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "Home", style = MaterialTheme.typography.headlineLarge) },
    )
}

@Composable
fun BottomNavBar(navController: NavHostController){
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home"){
                    popUpTo(navController.graph.findStartDestination().id){
                        saveState = false
                    }
                }
                      },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile"){
                    popUpTo(navController.graph.findStartDestination().id){
                        saveState = false
                    }
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
        )

   }
}

@Composable
fun TopicCard(topic: Topic, modifier: Modifier = Modifier, onTopicClick: (Topic) -> Unit){
    Card(
        modifier = modifier
            .padding(bottom = 8.dp)
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