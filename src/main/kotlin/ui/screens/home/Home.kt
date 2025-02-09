package ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ui.components.FolderPickerDialog
import java.io.File

@Composable
fun Home() {
    CompositionLocalProvider(
        TerminalConsole provides mutableStateOf("command : "),
        OpenFolderPicker provides mutableStateOf(false),
        EngineLocation provides mutableStateOf(File(System.getProperty("user.home"))),
    ) {

        val openFolderPicker = OpenFolderPicker.current
        val engineLocation = EngineLocation.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Sidebar()
                Chat()
            }
        }

        if (openFolderPicker.value) {
            FolderPickerDialog(onDismissRequest = { openFolderPicker.value = false }) { folder ->
                engineLocation.value = folder
            }
        }
    }
}

