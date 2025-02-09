package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.io.File

@Composable
fun FolderPickerDialog(
    onDismissRequest: () -> Unit,
    onFolderSelected: (File) -> Unit
) {
    var currentPath by remember { mutableStateOf(System.getProperty("user.home")) }
    var searchPath by remember { mutableStateOf(currentPath) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp)
            ) {
                Text(
                    text = "Select Folder",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = {
                            val parent = File(currentPath).parentFile
                            if (parent != null && parent.isDirectory) {
                                currentPath = parent.absolutePath
                                searchPath = currentPath
                            }
                        },
                        enabled = currentPath != File("/").absolutePath
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchPath,
                        onValueChange = {
                            searchPath = it
                            if (File(it).isDirectory) {
                                currentPath = it
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    FilePicker(
                        currentPath = currentPath,
                        onFolderSelected = { folder ->
                            currentPath = folder.absolutePath // Navigate into the selected folder
                            searchPath = currentPath
                        },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onFolderSelected(File(currentPath))
                            onDismissRequest()
                        },
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}

@Composable
fun FilePicker(
    currentPath: String,
    onFolderSelected: (File) -> Unit
) {
    val files = remember(currentPath) {
        File(currentPath).listFiles()?.filter { it.isDirectory } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        files.forEach { file ->
            TextButton(
                onClick = { onFolderSelected(file) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(file.name)
            }

            // Option to select a folder
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
