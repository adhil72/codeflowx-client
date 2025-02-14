package ui.screens.home

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import engine.FrameWorkDetector
import engine.LanguageDetector
import engine.ProjectDetector
import java.io.File

val TerminalConsole = compositionLocalOf { mutableStateOf("") }
val OpenFolderPicker = compositionLocalOf { mutableStateOf(false) }
val EngineLocation = compositionLocalOf { mutableStateOf(File("")) }
val SelectedProject = compositionLocalOf {
    mutableStateOf(
        ProjectDetector.Project(
            frameWork = FrameWorkDetector.FrameWork(FrameWorkDetector.FrameWorks.NA, "", "", listOf()),
            language = LanguageDetector.Language("", listOf())
        )
    )
}