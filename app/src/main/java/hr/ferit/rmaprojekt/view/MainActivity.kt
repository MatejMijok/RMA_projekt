package hr.ferit.rmaprojekt.view


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.rmaprojekt.data.repository.TopicRepository
import hr.ferit.rmaprojekt.data.repository.UserRepository
import hr.ferit.rmaprojekt.ui.theme.RMAProjektTheme
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.viewmodel.TopicViewModelFactory
import hr.ferit.rmaprojekt.viewmodel.UserViewModel
import hr.ferit.rmaprojekt.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    private val topicRepository = TopicRepository()
    private val userRepository = UserRepository()
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topicViewModel = ViewModelProvider(this, TopicViewModelFactory(topicRepository))[TopicViewModel::class.java]
        userViewModel = ViewModelProvider(this, UserViewModelFactory(userRepository))[UserViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            RMAProjektTheme {
                val navController: NavHostController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController, topicViewModel, userViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    topicViewModel: TopicViewModel,
    userViewModel: UserViewModel
){
    var currentUser = FirebaseAuth.getInstance().currentUser
    NavHost(navController = navController, startDestination = if (currentUser != null ) "home" else "welcome"){
            composable(
                "welcome",
                enterTransition = {
                    slideInHorizontally (
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally (
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                }
                ) { WelcomeScreen(navController) }
            composable(
                "home",
                enterTransition = {
                    slideInHorizontally (
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally (
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                }) { HomeScreen(navController, Modifier, userViewModel, topicViewModel) }
            composable(
                "login",
                enterTransition = {
                    slideInHorizontally (
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally (
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                }) { LoginScreen(navController, Modifier, userViewModel) }
            composable(
                "register",
                enterTransition = {
                    slideInHorizontally (
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally (
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ) { RegisterScreen(navController, Modifier, userViewModel) }
            composable(
                "addNewTopic",
                enterTransition = {
                    slideInHorizontally (
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally (
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            ) { AddNewTopic(navController, Modifier, topicViewModel) }
        composable(
            "topicDetails/{topicId}",
            enterTransition = {
                slideInHorizontally (
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutHorizontally (
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(durationMillis = 300)
                )
            }) { HomeScreen(navController, Modifier, userViewModel, topicViewModel) }
        }
}

@Composable
fun WelcomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
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
            onClick = { navController.navigate("login") },
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
            onClick = { navController.navigate("register") },
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