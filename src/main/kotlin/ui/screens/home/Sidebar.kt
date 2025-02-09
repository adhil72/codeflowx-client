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
                text = "Navigation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = false,
                onClick = { /* Handle home click */ },
                modifier = Modifier.fillMaxWidth()
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Menu, contentDescription = "Change Directory") },
                label = { Text("Change Directory") },
                selected = false,
                onClick = {

                },
                modifier = Modifier.fillMaxWidth()
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                selected = false,
                onClick = { /* Handle settings click */ },
                modifier = Modifier.fillMaxWidth()
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                label = { Text("About") },
                selected = false,
                onClick = { /* Handle about click */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Divider()

            Text(
                text = "Current Directory:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "currentDir.path",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

