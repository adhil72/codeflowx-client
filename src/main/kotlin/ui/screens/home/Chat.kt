package ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import engine.Engine
import engine.Terminal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        if (message.showName) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message.sender.trim(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        message.content()
    }
}

data class ChatMessage(
    val sender: String,
    val content: @Composable () -> Unit,
    val showName: Boolean = true,
    val isTyping: Boolean = false
)

suspend fun executeTerminalCommand(script: String, engineLocation: File, onUpdate: (String) -> Unit) {
    suspendCancellableCoroutine<Unit> { continuation ->
        Terminal(engineLocation).executeCommand(script) {
            onUpdate(it)
            continuation.resumeWith(Result.success(Unit))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat() {
    val messageInput = remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val engineLocation = EngineLocation.current
    val engine = Engine(engineLocation.value)
    val openFolderPickerDialog = remember { mutableStateOf(true) }
    val projectCompo = SelectedProject.current

    LaunchedEffect(engine.project) {
        if (engine.project != null) projectCompo.value = engine.project!!
    }

    val listState = rememberLazyListState()

    if (openFolderPickerDialog.value) {
        SelectFolderDialogue({ openFolderPickerDialog.value = false }) {
            engineLocation.value = File(it)
            CoroutineScope(Dispatchers.Default).launch {
                openFolderPickerDialog.value = false
            }
        }
    }

    fun sendMessage(isTerminalCommand: Boolean = false) {
        if (isTerminalCommand) {
            val msg = messageInput.value
            messageInput.value = ""
            messages.add(ChatMessage("You", { Text(msg, color = Color.White) }))
            messages.add(ChatMessage("Terminal", { Text("") }))
            val terminal = Terminal(engineLocation.value)

            val typingIndicator = ChatMessage("", { Text("...") }, isTyping = true)
            messages.add(typingIndicator)

            terminal.executeCommand(messageInput.value) {
                messages.remove(typingIndicator)
                messages.add(ChatMessage("TR", { Text(it) }, false))
                messages.add(typingIndicator)
            }
            messages.remove(typingIndicator)
            messages.add(ChatMessage("TR", { Text("Command executed successfully") }, false))
            return
        }

        if (messageInput.value.isNotBlank()) {
            messages.add(ChatMessage("You", { bodyText(messageInput.value) }))
            messages.add(ChatMessage("CodeFlowX", { Text("") }))
            val msg = messageInput.value
            messageInput.value = ""
            var firstResponse = true

            val typingIndicator = ChatMessage("", { Text("...") }, isTyping = true)
            messages.add(typingIndicator)

            CoroutineScope(Dispatchers.Default).launch {
                engine.execute(msg) {
                    if (firstResponse) {
                        messages.remove(typingIndicator)
                        firstResponse = false
                    }
                    if (it.startsWith("sh")) {
                        val script = it.replace("sh\n", "").trimEnd('\n')
                        messages.add(ChatMessage("TR", { shBox(script) }, false))
                        CoroutineScope(Dispatchers.Default).launch {
                            executeTerminalCommand(script, engineLocation.value) { result ->
                                messages.add(ChatMessage("TR", { terminalTextBox(result) }, false))
                            }
                        }
                    } else {
                        messages.add(ChatMessage("TR", { bodyText(it) }, false))
                    }
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.size > 0) listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat header
        headerText("Chat")

        // Current directory
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Current Directory: ${engineLocation.value}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState
        ) {
            items(messages) { message ->
                if (message.isTyping) {
                    TypingIndicator()
                } else {
                    ChatMessageItem(message)
                }
            }
        }

        // Input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInput.value,
                onValueChange = { messageInput.value = it },
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text("Type a message...") },
                modifier = Modifier
                    .weight(1f)
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter && it.isCtrlPressed) {
                            if (it.isShiftPressed) {
                                sendMessage(true)
                            } else {
                                sendMessage()
                            }
                            true
                        } else {
                            false
                        }
                    }
            )
            IconButton(
                onClick = {
                    if (messageInput.value.isNotBlank()) {
                        sendMessage()
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
        )
    }
}

@Composable
fun headerText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun bodyText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White
    )
}

@Composable
fun shBox(script: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(8.dp)
            .heightIn(max = 150.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            script,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun terminalTextBox(text: String){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(8.dp)
            .heightIn(max = 150.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}