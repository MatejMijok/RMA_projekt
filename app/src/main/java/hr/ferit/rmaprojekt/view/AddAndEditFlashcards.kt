package hr.ferit.rmaprojekt.view

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel
import kotlinx.coroutines.launch

class AddNewFlashcardsActivity : ComponentActivity() {
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AddNewTopic(navController: NavHostController, modifier: Modifier = Modifier, topicViewModel: TopicViewModel) {
    val topic = topicViewModel.topicWithFlashcards

    var topicName by remember { mutableStateOf(TextFieldValue(topic?.topic?.name ?: "")) }
    var topicDescription by remember { mutableStateOf(TextFieldValue(topic?.topic?.description ?: "")) }

    var topicError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }

    var isTopicValid by remember { mutableStateOf(true) }
    var isDescriptionValid by remember { mutableStateOf(true) }

    var flashcards by remember { mutableStateOf(mutableStateListOf<Flashcard>()) }

    if(topic != null){
        flashcards.clear()
        flashcards.addAll(topic.flashcards)
    }else{
        flashcards.clear()
        flashcards.add(Flashcard())
    }

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Scaffold (
        modifier = modifier.fillMaxSize(),
        topBar = { NewFlashcardTopBar() },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 400.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = topicName,
                onValueChange = {
                    topicName = it
                    topicError = "Topic cannot be empty"
                    isTopicValid = it.text.isNotEmpty()
                },
                placeholder = {Text(text = "Topic name")},
                shape = RoundedCornerShape(15.dp),
                modifier = modifier
                    .padding(bottom = 14.dp)
                    .widthIn(max = 280.dp),
                singleLine = true,
                isError = !isTopicValid,
                supportingText = {if (!isTopicValid) Text(text = topicError)}
            )
            OutlinedTextField(
                value = topicDescription,
                onValueChange = {
                    topicDescription = it
                    descriptionError = "Description cannot be empty"
                    isDescriptionValid = it.text.isNotEmpty()
                },
                placeholder = {Text(text = "Description")},
                shape = RoundedCornerShape(15.dp),
                modifier = modifier
                    .padding(bottom = 8.dp)
                    .widthIn(max = 280.dp),
                singleLine = false,
                isError = !isDescriptionValid,
                supportingText = {if (!isDescriptionValid) Text(text = descriptionError)}
            )
            Text(
                text = "Flashcards",
                style = MaterialTheme.typography.headlineMedium,
                modifier = modifier.padding(bottom = 14.dp)
            )
            HorizontalPager(
                count = flashcards.size,
                state = pagerState,
                modifier = modifier
                    .fillMaxWidth()
            ){  page ->
                FlashcardInput(
                    index = page,
                    flashcard = flashcards.getOrElse(page) { Flashcard() },
                    onFlashcardChange = { updatedFlashcard ->
                        if(page < flashcards.size){
                            flashcards[page] = updatedFlashcard
                        }
                    },
                    onDelete = {
                        if(flashcards.size > 1){
                            flashcards.removeAt(page)
                            if(pagerState.currentPage >= flashcards.size){
                                scope.launch{
                                    pagerState.animateScrollToPage(flashcards.size - 1)
                                }
                            }
                        }
                    },
                    deleteEnabled = flashcards.size > 1,
                    topicViewModel = topicViewModel
                )
            }
            Button(
                onClick = {
                    flashcards.add(Flashcard())
                    scope.launch {
                        pagerState.animateScrollToPage(flashcards.size-1)
                    }
                          },
                modifier = modifier
                    .width(192.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            ) {
                Text(
                    text = "Add flashcard",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    val immutableFlashcards = flashcards.toList()
                    if (topic != null){
                        topicViewModel.topicWithFlashcards = null
                        val updatedTopic = topic.copy(topic = Topic(name = topicName.text, description = topicDescription.text, id = topic.topic.id, creatorId = topic.topic.creatorId))
                        topicViewModel.updateTopicWithFlashcards(updatedTopic.topic, immutableFlashcards)
                    }else{
                        val newTopic = Topic(name = topicName.text, description = topicDescription.text)
                        topicViewModel.saveTopicWithFlashcards(newTopic, immutableFlashcards)
                    }

                    topicViewModel.clearTopics()

                    navController.navigate("home"){
                        popUpTo(0) { inclusive = true }
                    }
                },
                enabled = topicName.text.isNotEmpty() && topicDescription.text.isNotEmpty() && flashcards.isNotEmpty() && flashcards.all { it.question.isNotEmpty() && it.answer.isNotEmpty() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = modifier
                    .width(192.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = if (topic == null) "Create topic" else "Edit topic",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }

}

@Composable
fun FlashcardInput(
    index: Int,
    flashcard: Flashcard,
    onFlashcardChange: (Flashcard) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    deleteEnabled: Boolean,
    topicViewModel: TopicViewModel
) {
    val context = LocalContext.current

    var question by remember { mutableStateOf(TextFieldValue(flashcard.question)) }
    var answer by remember { mutableStateOf(TextFieldValue(flashcard.answer)) }

    var questionError by remember { mutableStateOf("") }
    var answerError by remember { mutableStateOf("") }

    var isQuestionValid by remember { mutableStateOf(true) }
    var isAnswerValid by remember { mutableStateOf(true) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(flashcard.imageUrl) }

    var shouldDeleteImage by remember { mutableStateOf(imageUrl.isNotEmpty()) }
    var loading by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri != null) {
            loading = true
            val imageStream = context.contentResolver.openInputStream(selectedImageUri!!)
            imageUrl = topicViewModel.uploadImage(imageStream!!).toString()
            loading = false
            shouldDeleteImage = true
            onFlashcardChange(flashcard.copy(imageUrl = imageUrl))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 400.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${index + 1}. flashcard",
            fontSize = 24.sp,
            modifier = modifier.padding(bottom = 14.dp)
        )
        OutlinedTextField(
            value = question,
            onValueChange = {
                question = it
                questionError = "Question cannot be empty"
                isQuestionValid = it.text.isNotEmpty()
                onFlashcardChange(flashcard.copy(question = it.text))
            },
            placeholder = { Text(text = "Question") },
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 14.dp)
                .widthIn(max = 280.dp),
            singleLine = true,
            isError = !isQuestionValid,
            supportingText = { if (!isQuestionValid) Text(text = questionError) }
        )
        OutlinedTextField(
            value = answer,
            onValueChange = {
                answer = it
                answerError = "Answer cannot be empty"
                isAnswerValid = it.text.isNotEmpty()
                onFlashcardChange(flashcard.copy(answer = it.text))
            },
            placeholder = { Text(text = "Answer") },
            shape = RoundedCornerShape(15.dp),
            modifier = modifier
                .padding(bottom = 14.dp)
                .widthIn(max = 280.dp),
            singleLine = false,
            isError = !isAnswerValid,
            supportingText = { if (!isAnswerValid) Text(text = answerError) }
        )
        Button(
            onClick = {
                if (!shouldDeleteImage) {
                    galleryLauncher.launch("image/*")
                } else {
                    topicViewModel.deleteImage(imageUrl)
                    imageUrl = ""
                    onFlashcardChange(flashcard.copy(imageUrl = imageUrl))
                    shouldDeleteImage = false
                }
            },
            modifier = modifier
                .width(192.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (!shouldDeleteImage) "Add image" else "Delete image",
                    fontSize = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                onDelete()
                imageUrl = ""
                onFlashcardChange(flashcard.copy(imageUrl = imageUrl))
                topicViewModel.deleteImage(imageUrl)
            },
            modifier = modifier
                .width(192.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            enabled = deleteEnabled
        ) {
            Text("Delete flashcard", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFlashcardTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Add new topic",
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}