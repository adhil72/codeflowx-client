package engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class Engine(private val folder: File) {
    fun execute(command: String, onUpdate: (String) -> Unit) {

    }
}