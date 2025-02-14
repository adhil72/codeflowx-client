package ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sidebar() {
    val openFolderPicker = OpenFolderPicker.current
    val currentDir = EngineLocation.current
    val project = SelectedProject.current

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Project details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NavigationDrawerItem(
                label = { Text(project.value.frameWork.name.toString()) },
                selected = false,
                onClick = { /* Handle home click */ },
                modifier = Modifier.fillMaxWidth()
            )

            NavigationDrawerItem(
                label = { Text("${project.value.frameWork.version}") },
                selected = false,
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
            )

            NavigationDrawerItem(
                label = { Text(project.value.language.name) },
                selected = false,
                onClick = { /* Handle settings click */ },
                modifier = Modifier.fillMaxWidth()
            )

//            NavigationDrawerItem(
//                label = { Text(project.value.frameWork) },
//                selected = false,
//                onClick = { /* Handle about click */ },
//                modifier = Modifier.fillMaxWidth()
//            )

            Spacer(modifier = Modifier.weight(1f))

            Divider()
        }
    }
}

