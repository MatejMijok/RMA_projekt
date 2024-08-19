package hr.ferit.rmaprojekt.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hr.ferit.rmaprojekt.data.model.Flashcard
import hr.ferit.rmaprojekt.data.model.Topic
import hr.ferit.rmaprojekt.viewmodel.TopicViewModel

class AddNewFlashcardsActivity : ComponentActivity() {
}

@Composable
fun AddNewTopic(navController: NavHostController, modifier: Modifier = Modifier, topicViewModel: TopicViewModel) {
    var topicName by remember { mutableStateOf(TextFieldValue("")) }
    var topicDescription by remember { mutableStateOf(TextFieldValue("")) }

    var topicError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }

    var isTopicValid by remember { mutableStateOf(true) }
    var isDescriptionValid by remember { mutableStateOf(true) }

    var flashcards by remember { mutableStateOf(mutableStateListOf<Flashcard>())}
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
            Spacer(modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = topicName,
                onValueChange = {
                    topicName = it
                    topicError = "Topic cannot be empty"
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
                },
                placeholder = {Text(text = "Description")},
                shape = RoundedCornerShape(15.dp),
                modifier = modifier
                    .padding(bottom = 14.dp)
                    .widthIn(max = 280.dp),
                singleLine = false,
                isError = !isDescriptionValid,
                supportingText = {if (!isDescriptionValid) Text(text = descriptionError)}
            )
            Text(
                text = "Flashcards",
                fontSize = 36.sp,
                modifier = modifier.padding(bottom = 14.dp)
            )
            flashcards.forEachIndexed{ index, flashcard ->
                FlashcardInput(
                    index = index,
                    flashcard = flashcard,
                    onFlashcardChange = { updatedFlashcard ->
                        flashcards[index] = updatedFlashcard
                    },
                    onDelete = { flashcards.removeAt(index) }
                )
            }
            Button(
                onClick = { flashcards.add(Flashcard()) },
                modifier = modifier
                    .width(192.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B5C92),
                    contentColor = Color(0xFFDDE1F9)
                )
            ) {
                Text(
                    text = "Add flashcard",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    val topic = Topic(name = topicName.text, description = topicDescription.text)
                    topicViewModel.saveTopicWithFlashcards(topic, flashcards)
                    navController.navigate("home")
                },
                enabled = topicName.text.isNotEmpty() && topicDescription.text.isNotEmpty() && flashcards.isNotEmpty() && flashcards.all { it.question.isNotEmpty() && it.answer.isNotEmpty() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B5C92),
                    contentColor = Color(0xFFDDE1F9)
                ),
                modifier = modifier
                    .width(192.dp)
                    .height(48.dp)
            ) {
                Text(
                    "Create topic",
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
    modifier: Modifier = Modifier
){
    var question by remember { mutableStateOf(TextFieldValue("")) }
    var answer by remember { mutableStateOf(TextFieldValue("")) }

    var questionError by remember { mutableStateOf("") }
    var answerError by remember { mutableStateOf("") }

    var isQuestionValid by remember { mutableStateOf(true) }
    var isAnswerValid by remember { mutableStateOf(true) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 400.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${index+1}. flashcard",
            fontSize = 24.sp,
            modifier = modifier.padding(bottom = 14.dp)
        )
        OutlinedTextField(
            value = question,
            onValueChange = {
                question = it
                questionError = "Question cannot be empty"
                onFlashcardChange(flashcard.copy(question = question.text))
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
                onFlashcardChange(flashcard.copy(answer = answer.text))
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
            onClick = { onDelete() },
            modifier = modifier
                .width(192.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
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