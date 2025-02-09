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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import engine.Engine
import engine.Terminal

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
        if (message.content.isNotBlank()) Text(
            text = message.content.trim(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = if (message.sender == "TR") FontFamily.Monospace else FontFamily.Default,
        )
    }
}

data class ChatMessage(
    val sender: String,
    val content: String,
    val showName: Boolean = true,
    val isTyping: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat() {
    val messageInput = remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val engineLocation = EngineLocation.current
    val engine = Engine(engineLocation.value)
    val listState = rememberLazyListState()

    fun sendMessage(isTerminalCommand: Boolean = false) {
        if (isTerminalCommand) {
            val msg = messageInput.value
            messageInput.value = ""
            messages.add(ChatMessage("You", msg))
            messages.add(ChatMessage("Terminal", ""))
            val terminal = Terminal(engineLocation.value)

            val typingIndicator = ChatMessage("", "...", isTyping = true)
            messages.add(typingIndicator)

            terminal.executeCommand(messageInput.value) {
                messages.remove(typingIndicator)
                messages.add(ChatMessage("TR", it, false))
                messages.add(typingIndicator)
            }
            messages.remove(typingIndicator)
            messages.add(ChatMessage("TR", "Command executed successfully", false))
            return
        }

        if (messageInput.value.isNotBlank()) {
            messages.add(ChatMessage("You", messageInput.value))
            messages.add(ChatMessage("CodeFlowX", ""))
            val msg = messageInput.value
            messageInput.value = ""
            var firstResponse = true

            val typingIndicator = ChatMessage("", "...", isTyping = true)
            messages.add(typingIndicator)

            Thread {
                engine.execute(msg) {
                    if (firstResponse) {
                        messages.remove(typingIndicator)
                        firstResponse = false
                    }
                    messages.add(ChatMessage("TR", it, false))
                }
            }.start()
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
        Text(
            "Chat",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

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