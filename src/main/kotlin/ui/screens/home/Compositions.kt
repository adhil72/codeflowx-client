package ui.screens.home

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import java.io.File

val TerminalConsole = compositionLocalOf { mutableStateOf("") }
val OpenFolderPicker = compositionLocalOf { mutableStateOf(false) }
val EngineLocation = compositionLocalOf { mutableStateOf(File("")) }
