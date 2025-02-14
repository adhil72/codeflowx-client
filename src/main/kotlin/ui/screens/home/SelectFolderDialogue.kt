package ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.components.FolderPickerDialog

@Composable
fun SelectFolderDialogue(
    onDismiss: () -> Unit,
    onFolderSelected: (String) -> Unit
) {
    val selectedFolder = remember { mutableStateOf<String?>(null) }
    val openFolderPickerDialog = remember { mutableStateOf(false) }

    if (openFolderPickerDialog.value) {
        FolderPickerDialog(
            onDismissRequest = { openFolderPickerDialog.value = false },
            onFolderSelected = { folder ->
                selectedFolder.value = folder.path
            }
        )
    }

    AlertDialog(
        modifier = Modifier.background(Color.Black),
        backgroundColor = Color.Black,
        onDismissRequest = onDismiss,
        title = { Text("Select Folder") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.Black),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Please select a folder to proceed.", color = Color.White)
                Button(
                    onClick = {
                        openFolderPickerDialog.value = true
                    }
                ) {
                    Text("Choose Folder")
                }
                selectedFolder.value?.let {
                    Text("Selected: $it", color = Color.White)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedFolder.value?.let { onFolderSelected(it) }
                    onDismiss()
                },
                enabled = selectedFolder.value != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}