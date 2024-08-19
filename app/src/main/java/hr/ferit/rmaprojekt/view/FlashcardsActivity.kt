package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import hr.ferit.rmaprojekt.data.repository.Result

class FlashcardsActivity : ComponentActivity() {

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FlashcardsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    topicViewModel: TopicViewModel,
    topicId: String,
    ){
    val topicsWithFlashcards = topicViewModel.topicsWithFlashcards.collectAsState().value
    val topicWithFlashcards = (topicsWithFlashcards as Result.Success).data.find { it.topic.id == topicId }

    if(topicWithFlashcards != null){
        val flashcards = topicWithFlashcards.flashcards
        val pagerState = rememberPagerState ( initialPage = 0)
        Scaffold (
            modifier = modifier.fillMaxSize(),
            topBar = { TopBar(topicWithFlashcards.topic.name) }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                count = flashcards.size,
                modifier = modifier.fillMaxSize()
                    .padding(innerPadding)
                    .systemBarsPadding()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    FlashCard(frontContent = { Text(text = flashcards[page].question, style = MaterialTheme.typography.headlineSmall) },
                        backContent = { Text(text = flashcards[page].answer, style = MaterialTheme.typography.headlineSmall) })
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomCenter
            ){
                HorizontalPagerIndicator(pagerState = pagerState, activeColor = MaterialTheme.colorScheme.primary, inactiveColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            }
        }

    }else{
        Text(text = "Topic not found")
    }
}

@Composable
fun FlashCard(
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation = animateFloatAsState(targetValue = if (isFlipped) 180f else 0f, animationSpec = tween(durationMillis = 500))
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp,start = 16.dp, end = 16.dp, bottom = 64.dp)
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            }
            .clickable(interactionSource = interactionSource, indication = null) {
                isFlipped = !isFlipped
            },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHovered) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            if (rotation.value <= 90f) {
                frontContent()
            } else {
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    backContent()
                }
            }
        }
    }
}